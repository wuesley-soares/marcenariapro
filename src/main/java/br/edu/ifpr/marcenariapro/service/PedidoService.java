package br.edu.ifpr.marcenariapro.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.PedidoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PedidoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final OrcamentoRepository orcamentoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            OrcamentoRepository orcamentoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    public PedidoResponseDTO salvar(PedidoRequestDTO dto) {

        Orcamento orcamento = orcamentoRepository.findById(dto.orcamentoId())
                .orElseThrow(() ->
                        new RuntimeException("Orçamento não encontrado"));

        if (orcamento.getStatus() != StatusOrcamento.APROVADO) {
            throw new RuntimeException(
                    "Não é possível criar o pedido. "
                    + "O orçamento precisa estar aprovado.");
        }

        if (dto.status() != StatusPedido.PENDENTE) {
            throw new RuntimeException(
                    "Um novo pedido deve iniciar com o status PENDENTE.");
        }

        if (dto.previsaoEntrega() == null) {
            throw new RuntimeException(
                    "A previsão de entrega é obrigatória.");
        }

        if (dto.previsaoEntrega().isBefore(LocalDate.now())) {
    throw new RuntimeException(
        "A previsão de entrega não pode ser anterior à data atual."
    );
}

        if (pedidoRepository.findByOrcamentoId(dto.orcamentoId()).isPresent()) {
            throw new RuntimeException(
                    "Não é possível criar o pedido. "
                    + "Este orçamento já possui um pedido.");
        }

        Pedido pedido = new Pedido();

        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setPrevisaoEntrega(dto.previsaoEntrega());
        pedido.setOrcamento(orcamento);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return converterParaResponse(pedidoSalvo);
    }

    public List<PedidoResponseDTO> listar() {

        return pedidoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        return converterParaResponse(pedido);
    }

    public PedidoResponseDTO atualizar(
            Long id,
            PedidoRequestDTO dto) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        if (!pedido.getOrcamento().getId().equals(dto.orcamentoId())) {
            throw new RuntimeException(
                    "Não é possível alterar o orçamento de um pedido existente.");
        }

        /*
         * Pedido entregue não pode ser alterado.
         */
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RuntimeException(
                    "Não é possível alterar um pedido que já foi entregue.");
        }

        /*
         * Pedido cancelado não pode voltar para outro status.
         */
        if (pedido.getStatus() == StatusPedido.CANCELADO
                && dto.status() != StatusPedido.CANCELADO) {

            throw new RuntimeException(
                    "Não é possível alterar um pedido que já foi cancelado.");
        }

        /*
         * Pedido aprovado não pode voltar para pendente.
         */
        if (pedido.getStatus() == StatusPedido.APROVADO
                && dto.status() == StatusPedido.PENDENTE) {

            throw new RuntimeException(
                    "Não é possível voltar um pedido aprovado para PENDENTE.");
        }

        /*
         * Um pedido pendente só pode:
         * - continuar pendente
         * - ser aprovado
         * - ser cancelado
         */
        if (pedido.getStatus() == StatusPedido.PENDENTE
                && dto.status() == StatusPedido.ENTREGUE) {

            throw new RuntimeException(
                    "Um pedido pendente não pode ser entregue.");
        }

        /*
         * Um pedido aprovado pode:
         * - continuar aprovado
         * - ser entregue
         * - ser cancelado
         */
        if (pedido.getStatus() == StatusPedido.APROVADO
                && dto.status() == StatusPedido.ENTREGUE) {

            pedido.setDataEntrega(java.time.LocalDate.now());
        }

        /*
         * Não permite alterar a previsão depois que o pedido foi entregue.
         */
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RuntimeException(
                    "Não é possível alterar um pedido que já foi entregue.");
        }

        if (dto.previsaoEntrega() == null) {
    throw new RuntimeException(
            "A previsão de entrega é obrigatória.");
}

if (dto.previsaoEntrega().isBefore(LocalDate.now())) {
    throw new RuntimeException(
            "A previsão de entrega não pode ser anterior à data atual.");
}

        pedido.setStatus(dto.status());
        pedido.setPrevisaoEntrega(dto.previsaoEntrega());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        return converterParaResponse(pedidoAtualizado);
    }

    public void excluir(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.ENTREGUE
                || pedido.getStatus() == StatusPedido.CANCELADO) {

            throw new RuntimeException(
                    "Não é possível excluir um pedido entregue ou cancelado.");
        }

        pedidoRepository.delete(pedido);
    }

    private PedidoResponseDTO converterParaResponse(Pedido pedido) {

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getPrevisaoEntrega(),
                pedido.getDataEntrega(),
                pedido.getOrcamento().getId()
        );
    }
}