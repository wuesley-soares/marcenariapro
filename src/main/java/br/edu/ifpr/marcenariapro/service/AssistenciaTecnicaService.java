package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.entity.AssistenciaTecnica;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.entity.ItemAssistenciaTecnica;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import br.edu.ifpr.marcenariapro.repository.AssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.ItemAssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;

@Service
@Transactional
public class AssistenciaTecnicaService {


private final ImagemAssistenciaTecnicaService imagemAssistenciaTecnicaService;
    private final AssistenciaTecnicaRepository assistenciaRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemAssistenciaTecnicaRepository itemAssistenciaRepository;
    private final MaterialRepository materialRepository;
    private final ReservaMaterialService reservaMaterialService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

public AssistenciaTecnicaService(
        AssistenciaTecnicaRepository assistenciaRepository,
        ClienteRepository clienteRepository,
        PedidoRepository pedidoRepository,
        UsuarioRepository usuarioRepository,
        ItemAssistenciaTecnicaRepository itemAssistenciaRepository,
        MaterialRepository materialRepository,
        ReservaMaterialService reservaMaterialService,
        MovimentacaoEstoqueService movimentacaoEstoqueService,
        ImagemAssistenciaTecnicaService imagemAssistenciaTecnicaService) {

    this.assistenciaRepository = assistenciaRepository;
    this.clienteRepository = clienteRepository;
    this.pedidoRepository = pedidoRepository;
    this.usuarioRepository = usuarioRepository;
    this.itemAssistenciaRepository = itemAssistenciaRepository;
    this.materialRepository = materialRepository;
    this.reservaMaterialService = reservaMaterialService;
    this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    this.imagemAssistenciaTecnicaService = imagemAssistenciaTecnicaService;
}

    public AssistenciaTecnicaResponseDTO salvar(
            AssistenciaTecnicaRequestDTO dto) {

        validarDataEntrega(dto.dataEntregaOriginal());

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        Pedido pedido = null;

        if (dto.pedidoId() != null) {
            pedido = pedidoRepository.findById(dto.pedidoId())
                    .orElseThrow(() ->
                            new RuntimeException("Pedido não encontrado"));
        }

        Usuario funcionario = usuarioRepository.findById(dto.funcionarioId())
                .orElseThrow(() ->
                        new RuntimeException("Funcionário não encontrado"));

        StatusAssistencia status = dto.status() == null
                ? StatusAssistencia.ABERTA
                : dto.status();

        if (status != StatusAssistencia.ABERTA
                && status != StatusAssistencia.AGENDADA) {

            throw new RuntimeException(
                    "Uma nova assistência deve iniciar como ABERTA ou AGENDADA."
            );
        }

        AssistenciaTecnica assistencia = new AssistenciaTecnica();

        assistencia.setDataSolicitacao(dto.dataSolicitacao());
        assistencia.setDataAgendamento(dto.dataAgendamento());
        assistencia.setDescricao(dto.descricao());
        assistencia.setDataEntregaOriginal(dto.dataEntregaOriginal());
        assistencia.setValorServico(dto.valorServico());
        assistencia.setValorMateriais(0.0);
        assistencia.setStatus(status);
        assistencia.setCliente(cliente);
        assistencia.setPedido(pedido);
        assistencia.setFuncionario(funcionario);

        verificarGarantia(assistencia);

        return converterParaResponse(
                assistenciaRepository.save(assistencia)
        );
    }

    @Transactional(readOnly = true)
    public List<AssistenciaTecnicaResponseDTO> listar() {

        return assistenciaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssistenciaTecnicaResponseDTO buscarPorId(Long id) {

        AssistenciaTecnica assistencia = buscarAssistencia(id);

        return converterParaResponse(assistencia);
    }

    public AssistenciaTecnicaResponseDTO atualizar(
            Long id,
            AssistenciaTecnicaRequestDTO dto) {

        AssistenciaTecnica assistencia = buscarAssistencia(id);

        validarDataEntrega(dto.dataEntregaOriginal());

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        Pedido pedido = null;

        if (dto.pedidoId() != null) {
            pedido = pedidoRepository.findById(dto.pedidoId())
                    .orElseThrow(() ->
                            new RuntimeException("Pedido não encontrado"));
        }

        Usuario funcionario = usuarioRepository.findById(dto.funcionarioId())
                .orElseThrow(() ->
                        new RuntimeException("Funcionário não encontrado"));

        if (assistencia.getStatus() == StatusAssistencia.CONCLUIDA
                || assistencia.getStatus() == StatusAssistencia.CANCELADA) {

            throw new RuntimeException(
                    "Não é possível editar uma assistência concluída ou cancelada."
            );
        }

        assistencia.setDataSolicitacao(dto.dataSolicitacao());
        assistencia.setDataAgendamento(dto.dataAgendamento());
        assistencia.setDescricao(dto.descricao());
        assistencia.setDataEntregaOriginal(dto.dataEntregaOriginal());
        assistencia.setCliente(cliente);
        assistencia.setPedido(pedido);
        assistencia.setFuncionario(funcionario);

        if (dto.valorServico() != null && dto.valorServico() < 0) {
            throw new RuntimeException(
                    "O valor do serviço não pode ser negativo."
            );
        }

        assistencia.setValorServico(dto.valorServico());

        if (dto.status() != null
                && dto.status() != assistencia.getStatus()) {

            alterarStatus(assistencia, dto.status());
        }

        calcularValorMateriais(assistencia);

        return converterParaResponse(
                assistenciaRepository.save(assistencia)
        );
    }

    public ItemAssistenciaTecnicaResponseDTO adicionarMaterial(
            Long assistenciaId,
            ItemAssistenciaTecnicaRequestDTO dto) {

        AssistenciaTecnica assistencia = buscarAssistencia(assistenciaId);

        validarPermissaoAlterarMateriais(assistencia);

        if (dto.quantidade() == null || dto.quantidade() <= 0) {
            throw new RuntimeException(
                    "A quantidade deve ser maior que zero."
            );
        }

        Material material = materialRepository.findById(dto.materialId())
                .orElseThrow(() ->
                        new RuntimeException("Material não encontrado"));

        ItemAssistenciaTecnica item = new ItemAssistenciaTecnica();

        item.setQuantidade(dto.quantidade());
        item.setAssistencia(assistencia);
        item.setMaterial(material);

        ItemAssistenciaTecnica salvo =
                itemAssistenciaRepository.save(item);

        // Reserva o material sem alterar o estoque físico.
        // Se faltar, cria uma necessidade de compra.
        reservaMaterialService.reservarItemAssistencia(salvo);

        calcularValorMateriais(assistencia);

        assistenciaRepository.save(assistencia);

        return converterItemParaResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ItemAssistenciaTecnicaResponseDTO> listarMateriais(
            Long assistenciaId) {

        AssistenciaTecnica assistencia = buscarAssistencia(assistenciaId);

        return itemAssistenciaRepository
                .findByAssistenciaId(assistencia.getId())
                .stream()
                .map(this::converterItemParaResponse)
                .toList();
    }

    public void excluirMaterial(
            Long assistenciaId,
            Long itemId) {

        AssistenciaTecnica assistencia = buscarAssistencia(assistenciaId);

        validarPermissaoAlterarMateriais(assistencia);

        ItemAssistenciaTecnica item =
                itemAssistenciaRepository.findById(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Material da assistência não encontrado"
                                ));

        if (!item.getAssistencia().getId().equals(assistenciaId)) {
            throw new RuntimeException(
                    "O material não pertence a esta assistência."
            );
        }

        // O material ainda não foi consumido.
        // Portanto, não existe entrada física a registrar.
        reservaMaterialService.removerReservaAssistencia(item.getId());

        itemAssistenciaRepository.delete(item);

        calcularValorMateriais(assistencia);

        assistenciaRepository.save(assistencia);
    }

    public void excluir(Long id) {

    AssistenciaTecnica assistencia = buscarAssistencia(id);

    validarPermissaoAlterarMateriais(assistencia);

    // Exclui os arquivos e registros das imagens.
    imagemAssistenciaTecnicaService.excluirTodas(id);

    List<ItemAssistenciaTecnica> itens =
            itemAssistenciaRepository.findByAssistenciaId(id);

    for (ItemAssistenciaTecnica item : itens) {
        reservaMaterialService.removerReservaAssistencia(item.getId());
    }

    itemAssistenciaRepository.deleteByAssistenciaId(id);

    assistenciaRepository.delete(assistencia);
}
    private void alterarStatus(
            AssistenciaTecnica assistencia,
            StatusAssistencia novoStatus) {

        StatusAssistencia atual = assistencia.getStatus();

        if (atual == novoStatus) {
            return;
        }

        if (atual == StatusAssistencia.CONCLUIDA
                || atual == StatusAssistencia.CANCELADA) {

            throw new RuntimeException(
                    "Uma assistência concluída ou cancelada não pode ser alterada."
            );
        }

        if (novoStatus == StatusAssistencia.EM_ANDAMENTO) {

            if (atual != StatusAssistencia.ABERTA
                    && atual != StatusAssistencia.AGENDADA) {

                throw new RuntimeException(
                        "Não é possível iniciar a assistência neste status."
                );
            }

            iniciarAtendimento(assistencia);

        } else if (novoStatus == StatusAssistencia.CANCELADA) {

            cancelarAtendimento(assistencia);

        } else if (novoStatus == StatusAssistencia.CONCLUIDA) {

            if (atual != StatusAssistencia.EM_ANDAMENTO) {
                throw new RuntimeException(
                        "Só é possível concluir uma assistência em andamento."
                );
            }

        } else if (novoStatus == StatusAssistencia.AGENDADA
                || novoStatus == StatusAssistencia.ABERTA) {

            if (atual != StatusAssistencia.ABERTA
                    && atual != StatusAssistencia.AGENDADA) {

                throw new RuntimeException(
                        "Não é possível retornar a assistência para esse status."
                );
            }

        } else {
            throw new RuntimeException("Transição de status inválida.");
        }

        assistencia.setStatus(novoStatus);
    }

    private void iniciarAtendimento(AssistenciaTecnica assistencia) {

        List<ItemAssistenciaTecnica> itens =
                itemAssistenciaRepository.findByAssistenciaId(
                        assistencia.getId()
                );

        // O consumo de cada reserva e as saídas estão na mesma transação.
        // Se qualquer material estiver faltando, tudo será revertido.
        for (ItemAssistenciaTecnica item : itens) {

            reservaMaterialService.consumirReservaAssistencia(
                    item.getId()
            );

            movimentacaoEstoqueService.salvar(
                    new MovimentacaoEstoqueRequestDTO(
                            TipoMovimentacao.SAIDA,
                            item.getQuantidade(),
                            item.getMaterial().getId()
                    )
            );
        }
    }

    private void cancelarAtendimento(AssistenciaTecnica assistencia) {

        List<ItemAssistenciaTecnica> itens =
                itemAssistenciaRepository.findByAssistenciaId(
                        assistencia.getId()
                );

        if (assistencia.getStatus() == StatusAssistencia.EM_ANDAMENTO) {

            // Os materiais já saíram fisicamente.
            // Devolve cada quantidade por uma movimentação de entrada.
            for (ItemAssistenciaTecnica item : itens) {

                movimentacaoEstoqueService.salvar(
                        new MovimentacaoEstoqueRequestDTO(
                                TipoMovimentacao.ENTRADA,
                                item.getQuantidade(),
                                item.getMaterial().getId()
                        )
                );
            }

            // As reservas permanecem CONSUMIDAS como histórico.

        } else {

            // Ainda não houve consumo físico.
            // Apenas libera as reservas e cancela necessidades pendentes.
            for (ItemAssistenciaTecnica item : itens) {

                reservaMaterialService.liberarReservaAssistencia(
                        item.getId()
                );
            }
        }
    }

    private void validarPermissaoAlterarMateriais(
            AssistenciaTecnica assistencia) {

        if (assistencia.getStatus() != StatusAssistencia.ABERTA
                && assistencia.getStatus() != StatusAssistencia.AGENDADA) {

            throw new RuntimeException(
                    "Os materiais só podem ser alterados antes do início da assistência."
            );
        }
    }

    private AssistenciaTecnica buscarAssistencia(Long id) {

        return assistenciaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assistência técnica não encontrada"
                        ));
    }

    private void validarDataEntrega(LocalDate dataEntregaOriginal) {

        if (dataEntregaOriginal == null) {
            throw new RuntimeException(
                    "Informe a data de entrega original do pedido."
            );
        }
    }

    private void calcularValorMateriais(
            AssistenciaTecnica assistencia) {

        List<ItemAssistenciaTecnica> itens =
                itemAssistenciaRepository.findByAssistenciaId(
                        assistencia.getId()
                );

        BigDecimal total = BigDecimal.ZERO;

        for (ItemAssistenciaTecnica item : itens) {

            BigDecimal quantidade =
                    BigDecimal.valueOf(item.getQuantidade());

            BigDecimal valor =
                    item.getMaterial().getValorUnitario();

            if (valor != null) {
                total = total.add(quantidade.multiply(valor));
            }
        }

        assistencia.setValorMateriais(total.doubleValue());

        verificarGarantia(assistencia);
    }

    private void verificarGarantia(
            AssistenciaTecnica assistencia) {

        validarDataEntrega(assistencia.getDataEntregaOriginal());

        LocalDate limite =
                assistencia.getDataEntregaOriginal().plusYears(2);

        boolean dentro =
                !LocalDate.now().isAfter(limite);

        assistencia.setDentroGarantia(dentro);

        Double materiais = assistencia.getValorMateriais() == null
                ? 0.0
                : assistencia.getValorMateriais();

        Double servico = assistencia.getValorServico() == null
                ? 0.0
                : assistencia.getValorServico();

        if (dentro) {
            servico = 0.0;
        }

        assistencia.setValorServico(servico);
        assistencia.setValorMateriais(materiais);
        assistencia.setValorTotal(servico + materiais);
    }

    private ItemAssistenciaTecnicaResponseDTO converterItemParaResponse(
            ItemAssistenciaTecnica item) {

        return new ItemAssistenciaTecnicaResponseDTO(
                item.getId(),
                item.getQuantidade(),
                item.getMaterial().getId(),
                item.getMaterial().getNome()
        );
    }

    private AssistenciaTecnicaResponseDTO converterParaResponse(
            AssistenciaTecnica assistencia) {

        return new AssistenciaTecnicaResponseDTO(
                assistencia.getId(),
                assistencia.getDataSolicitacao(),
                assistencia.getDataAgendamento(),
                assistencia.getDescricao(),
                assistencia.getDataEntregaOriginal(),
                assistencia.getDentroGarantia(),
                assistencia.getValorServico(),
                assistencia.getValorMateriais(),
                assistencia.getValorTotal(),
                assistencia.getStatus(),
                assistencia.getCliente().getId(),
                assistencia.getPedido() != null
                        ? assistencia.getPedido().getId()
                        : null,
                assistencia.getFuncionario().getId()
        );
    }
}