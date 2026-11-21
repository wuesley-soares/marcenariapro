package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.MovimentacaoEstoque;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.MovimentacaoEstoqueRepository;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final MaterialRepository materialRepository;
    private final ReservaMaterialService reservaMaterialService;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoRepository,
            MaterialRepository materialRepository,
            ReservaMaterialService reservaMaterialService) {

        this.movimentacaoRepository = movimentacaoRepository;
        this.materialRepository = materialRepository;
        this.reservaMaterialService = reservaMaterialService;
    }

    /*
     * Movimentação normal:
     * - ENTRADA: aumenta o estoque físico.
     * - SAIDA: verifica o estoque disponível, descontando reservas.
     *
     * Continua sendo utilizada pela tela de movimentações
     * e pelas entradas de devolução.
     */
    @Transactional
    public MovimentacaoEstoqueResponseDTO salvar(
            MovimentacaoEstoqueRequestDTO dto) {

        validarMovimentacao(dto);

        Material material = buscarMaterial(dto.materialId());

        if (dto.tipo() == TipoMovimentacao.SAIDA) {

            BigDecimal disponivel =
                    reservaMaterialService.calcularDisponivel(material);

            if (disponivel.compareTo(dto.quantidade()) < 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estoque disponível insuficiente. "
                        + "Existem materiais reservados."
                );
            }

            return registrarSaidaFisica(
                    material,
                    dto.quantidade()
            );
        }

        if (dto.tipo() == TipoMovimentacao.ENTRADA) {

            return registrarEntradaFisica(
                    material,
                    dto.quantidade(),
                    true
            );
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Tipo de movimentação inválido."
        );
    }

    /*
     * Saída originada do consumo de uma reserva.
     *
     * Utilizada pela produção e pela assistência técnica.
     * A reserva deve ser consumida e validada antes desta chamada.
     */
    @Transactional
    public MovimentacaoEstoqueResponseDTO registrarSaidaConsumo(
            MovimentacaoEstoqueRequestDTO dto) {

        validarMovimentacao(dto);

        if (dto.tipo() != TipoMovimentacao.SAIDA) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Esta operação aceita somente saídas."
            );
        }

        Material material = buscarMaterial(dto.materialId());

        // Confere o estoque físico, sem descontar novamente as reservas.
        if (material.getQuantidade().compareTo(dto.quantidade()) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Estoque físico insuficiente para registrar o consumo "
                    + "do material: " + material.getNome()
            );
        }

        return registrarSaidaFisica(
                material,
                dto.quantidade()
        );
    }

    /*
     * Registra a saída física e salva o histórico.
     */
    private MovimentacaoEstoqueResponseDTO registrarSaidaFisica(
            Material material,
            BigDecimal quantidade) {

        material.setQuantidade(
                material.getQuantidade().subtract(quantidade)
        );

        materialRepository.save(material);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setTipo(TipoMovimentacao.SAIDA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setMaterial(material);

        MovimentacaoEstoque salva =
                movimentacaoRepository.save(movimentacao);

        return converterParaResponse(salva);
    }

    /*
     * Registra uma entrada física.
     *
     * atenderNecessidades indica se a entrada deve ser distribuída
     * entre as necessidades de compra pendentes.
     */
    private MovimentacaoEstoqueResponseDTO registrarEntradaFisica(
            Material material,
            BigDecimal quantidade,
            boolean atenderNecessidades) {

        material.setQuantidade(
                material.getQuantidade().add(quantidade)
        );

        materialRepository.save(material);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setMaterial(material);

        MovimentacaoEstoque salva =
                movimentacaoRepository.save(movimentacao);

        if (atenderNecessidades) {

            reservaMaterialService.atenderNecessidadesCompra(
                    material,
                    quantidade
            );
        }

        return converterParaResponse(salva);
    }

    /*
     * Busca material pelo ID.
     */
    private Material buscarMaterial(Long materialId) {

        return materialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Material não encontrado."
                ));
    }

    /*
     * Valida os dados da movimentação.
     */
    private void validarMovimentacao(
            MovimentacaoEstoqueRequestDTO dto) {

        if (dto == null
                || dto.tipo() == null
                || dto.materialId() == null
                || dto.quantidade() == null
                || dto.quantidade().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o tipo, o material e uma quantidade maior que zero."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponseDTO> listar() {

        return movimentacaoRepository.findAll()
                .stream()
                .sorted((a, b) ->
                        b.getDataMovimentacao()
                                .compareTo(a.getDataMovimentacao()))
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoEstoqueResponseDTO buscarPorId(Long id) {

        MovimentacaoEstoque movimentacao =
                movimentacaoRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Movimentação não encontrada."
                        ));

        return converterParaResponse(movimentacao);
    }

    public void excluir(Long id) {

        movimentacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Movimentação não encontrada."
                ));

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Não é possível excluir uma movimentação de estoque."
        );
    }

    private MovimentacaoEstoqueResponseDTO converterParaResponse(
            MovimentacaoEstoque movimentacao) {

        return new MovimentacaoEstoqueResponseDTO(
                movimentacao.getId(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataMovimentacao(),
                movimentacao.getMaterial().getId(),
                movimentacao.getMaterial().getNome()
        );
    }

    @Transactional
public MovimentacaoEstoqueResponseDTO registrarEntradaDevolucao(
        MovimentacaoEstoqueRequestDTO dto) {

    validarMovimentacao(dto);

    if (dto.tipo() != TipoMovimentacao.ENTRADA) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Esta operação aceita somente entradas."
        );
    }

    Material material = buscarMaterial(dto.materialId());

    return registrarEntradaFisica(
            material,
            dto.quantidade(),
            false
    );
}
}