package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.PedidoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PedidoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final PagamentoRepository pagamentoRepository;

 public PedidoService(
        PedidoRepository pedidoRepository,
        OrcamentoRepository orcamentoRepository,
        PagamentoRepository pagamentoRepository) {

    this.pedidoRepository = pedidoRepository;
    this.orcamentoRepository = orcamentoRepository;
    this.pagamentoRepository = pagamentoRepository;
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
            "Um novo pedido deve iniciar com o status PENDENTE."
    );
    }

    if (pedidoRepository.findByOrcamentoId(dto.orcamentoId()).isPresent()) {
        throw new RuntimeException(
                "Não é possível criar o pedido. "
                + "Este orçamento já possui um pedido.");
    }

    Pedido pedido = new Pedido();

    pedido.setStatus(dto.status());
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
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    return converterParaResponse(pedido);
}

public PedidoResponseDTO atualizar(
        Long id,
        PedidoRequestDTO dto) {

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    
            
     if (pedido.getStatus() == br.edu.ifpr.marcenariapro.enums.StatusPedido.ENTREGUE
         && dto.status() != br.edu.ifpr.marcenariapro.enums.StatusPedido.ENTREGUE) {

         throw new RuntimeException(
            "Não é possível alterar um pedido que já foi entregue."
     );
}

    if (pedido.getStatus() == StatusPedido.CANCELADO
        && dto.status() != StatusPedido.CANCELADO) {

    throw new RuntimeException(
            "Não é possível alterar um pedido que já foi cancelado."
    );
}

    if (pedido.getStatus() == StatusPedido.PRONTO
        && (dto.status() == StatusPedido.PENDENTE
        || dto.status() == StatusPedido.EM_PRODUCAO)) {

    throw new RuntimeException(
            "Não é possível voltar o pedido para um status anterior."
    );
}

if (pedido.getStatus() == StatusPedido.EM_PRODUCAO
        && dto.status() == StatusPedido.PENDENTE) {

    throw new RuntimeException(
            "Não é possível voltar um pedido em produção para PENDENTE."
    );
}

if (pedido.getStatus() == StatusPedido.PRONTO
        && dto.status() == StatusPedido.CANCELADO) {

    throw new RuntimeException(
            "Não é possível cancelar um pedido que já está pronto."
    );
}

if (pedido.getStatus() == StatusPedido.PENDENTE
        && dto.status() == StatusPedido.PRONTO) {

    throw new RuntimeException(
            "O pedido precisa passar por EM_PRODUCAO antes de ficar PRONTO."
    );
}

if (pedido.getStatus() == StatusPedido.PENDENTE
        && dto.status() == StatusPedido.ENTREGUE) {

    throw new RuntimeException(
            "O pedido precisa estar pronto antes de ser entregue."
    );
}

if (pedido.getStatus() == StatusPedido.EM_PRODUCAO
        && dto.status() == StatusPedido.ENTREGUE) {

    throw new RuntimeException(
            "O pedido precisa estar PRONTO antes de ser entregue."
    );
}

if (!pedido.getOrcamento().getId().equals(dto.orcamentoId())) {
    throw new RuntimeException(
            "Não é possível alterar o orçamento de um pedido existente."
    );
}

if (pedido.getStatus() == StatusPedido.PENDENTE
        && dto.status() == StatusPedido.EM_PRODUCAO) {

    BigDecimal valorPedido = pedido.getOrcamento().getValor();

    BigDecimal valorMinimoEntrada =
            valorPedido.multiply(new BigDecimal("0.30"));

    BigDecimal valorPago = pagamentoRepository
            .findByPedidoId(pedido.getId())
            .stream()
            .filter(pagamento ->
                    pagamento.getStatus() ==
                    br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO)
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (valorPago.compareTo(valorMinimoEntrada) < 0) {
        throw new RuntimeException(
                "Não é possível iniciar a produção. "
                + "O pedido precisa ter pelo menos 30% do valor pago."
        );
    }
}
    pedido.setStatus(dto.status());
    

    Pedido pedidoAtualizado = pedidoRepository.save(pedido);

    return converterParaResponse(pedidoAtualizado);
}

public void excluir(Long id) {

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    if (pedido.getStatus() == StatusPedido.ENTREGUE
            || pedido.getStatus() == StatusPedido.CANCELADO) {

        throw new RuntimeException(
                "Não é possível excluir um pedido entregue ou cancelado."
        );
    }

    pedidoRepository.delete(pedido);
}
    private PedidoResponseDTO converterParaResponse(Pedido pedido) {

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getOrcamento().getId()
        );
    }
}