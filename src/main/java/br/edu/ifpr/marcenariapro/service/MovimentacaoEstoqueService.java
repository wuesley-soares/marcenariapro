
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

    @Transactional
    public MovimentacaoEstoqueResponseDTO salvar(
            MovimentacaoEstoqueRequestDTO dto) {

        if (dto.tipo() == null || dto.materialId() == null
                || dto.quantidade() == null || dto.quantidade() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o tipo, o material e uma quantidade maior que zero."
            );
        }

        Material material = materialRepository.findById(dto.materialId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Material não encontrado."
                ));

        if (dto.tipo() == TipoMovimentacao.SAIDA) {

            BigDecimal disponivel =
                    reservaMaterialService.calcularDisponivel(material);

            if (disponivel.compareTo(
                    BigDecimal.valueOf(dto.quantidade())) < 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estoque disponível insuficiente. Existem materiais reservados."
                );
            }

            material.setQuantidade(
                    material.getQuantidade() - dto.quantidade()
            );
        }

        if (dto.tipo() == TipoMovimentacao.ENTRADA) {

            material.setQuantidade(
                    material.getQuantidade() + dto.quantidade()
            );
        }

        materialRepository.save(material);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setTipo(dto.tipo());
        movimentacao.setQuantidade(dto.quantidade());
        movimentacao.setMaterial(material);

        MovimentacaoEstoque movimentacaoSalva =
                movimentacaoRepository.save(movimentacao);

        if (dto.tipo() == TipoMovimentacao.ENTRADA) {

            reservaMaterialService.atenderNecessidadesCompra(
                    material,
                    BigDecimal.valueOf(dto.quantidade())
            );
        }

        return converterParaResponse(movimentacaoSalva);
    }

    public List<MovimentacaoEstoqueResponseDTO> listar() {

        return movimentacaoRepository.findAll()
                .stream()
                .sorted((a, b) ->
                        b.getDataMovimentacao()
                                .compareTo(a.getDataMovimentacao()))
                .map(this::converterParaResponse)
                .toList();
    }

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
}
