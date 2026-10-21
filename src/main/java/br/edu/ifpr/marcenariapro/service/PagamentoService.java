package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.FinanceiroPedidoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository) {

        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

 public PagamentoResponseDTO salvar(PagamentoRequestDTO dto) {

    Pedido pedido = pedidoRepository.findById(dto.pedidoId())
            .orElseThrow(() ->
                    new RuntimeException("Pedido não encontrado"));

    if (dto.valor() == null || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException(
                "O valor do pagamento deve ser maior que zero");
    }

    if (dto.status() != br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO) {
        throw new RuntimeException(
                "Um novo pagamento deve ser registrado como PAGO.");
    }

    BigDecimal valorPedido = pedido.getOrcamento().getValor();

    BigDecimal valorPago = pagamentoRepository
            .findByPedidoId(pedido.getId())
            .stream()
            .filter(pagamento ->
                    pagamento.getStatus() ==
                    br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO)
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal novoTotalPago = valorPago.add(dto.valor());

    // Não permite pagar mais que o valor do pedido
    if (novoTotalPago.compareTo(valorPedido) > 0) {
        throw new RuntimeException(
                "O pagamento ultrapassa o valor total do pedido");
    }

    Pagamento pagamento = new Pagamento();

    pagamento.setValor(dto.valor());

    if (dto.dataPagamento() != null) {
        pagamento.setDataPagamento(dto.dataPagamento());
    }

    pagamento.setDescricao(dto.descricao());
    pagamento.setStatus(dto.status());
    pagamento.setPedido(pedido);

    Pagamento salvo = pagamentoRepository.save(pagamento);

    /*
     * Se o total pago atingir pelo menos 30%,
     * o pedido passa de PENDENTE para APROVADO.
     */
    BigDecimal valorEntrada =
            valorPedido.multiply(new BigDecimal("0.30"));

    if (pedido.getStatus() == br.edu.ifpr.marcenariapro.enums.StatusPedido.PENDENTE
            && novoTotalPago.compareTo(valorEntrada) >= 0) {

        pedido.setStatus(
                br.edu.ifpr.marcenariapro.enums.StatusPedido.APROVADO);

        pedidoRepository.save(pedido);
    }

    return converterParaResponse(salvo);
}

    public List<PagamentoResponseDTO> listar() {

        return pagamentoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public List<PagamentoResponseDTO> listarPorPedido(Long pedidoId) {

        pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        return pagamentoRepository.findByPedidoId(pedidoId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    private PagamentoResponseDTO converterParaResponse(
            Pagamento pagamento) {

        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getValor(),
                pagamento.getDataPagamento(),
                pagamento.getDescricao(),
                pagamento.getStatus(),
                pagamento.getPedido().getId()
        );
    }

    public FinanceiroPedidoResponseDTO resumoFinanceiro(Long pedidoId) {

    Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() ->
                    new RuntimeException("Pedido não encontrado"));

    BigDecimal valorPedido = pedido.getOrcamento().getValor();

    BigDecimal valorEntrada = valorPedido
            .multiply(new BigDecimal("0.30"));

    BigDecimal valorPago = pagamentoRepository
            .findByPedidoId(pedidoId)
            .stream()
            .filter(pagamento ->
                    pagamento.getStatus() ==
                    br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO)
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal valorRestante = valorPedido.subtract(valorPago);

    String situacao;

    if (valorPago.compareTo(BigDecimal.ZERO) == 0) {
        situacao = "PENDENTE";
    } else if (valorPago.compareTo(valorPedido) >= 0) {
        situacao = "PAGO";
    } else {
        situacao = "PARCIALMENTE_PAGO";
    }

    return new FinanceiroPedidoResponseDTO(
            pedidoId,
            valorPedido,
            valorEntrada,
            valorPago,
            valorRestante,
            situacao
    );
}
}