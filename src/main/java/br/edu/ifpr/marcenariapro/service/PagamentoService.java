package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpr.marcenariapro.dto.FinanceiroPedidoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.enums.StatusPagamento;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final ComprovanteService comprovanteService;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            ComprovanteService comprovanteService) {

        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.comprovanteService = comprovanteService;
    }

    @Transactional
    public PagamentoResponseDTO salvar(
            PagamentoRequestDTO dto,
            MultipartFile comprovante) {

        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        if (dto.valor() == null ||
                dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "O valor do pagamento deve ser maior que zero");
        }

        if (dto.status() != StatusPagamento.PAGO) {
            throw new RuntimeException(
                    "Um novo pagamento deve ser registrado como PAGO.");
        }

        BigDecimal valorPedido = pedido.getOrcamento().getValor();

        BigDecimal valorPago = pagamentoRepository
                .findByPedidoId(pedido.getId())
                .stream()
                .filter(p -> p.getStatus() == StatusPagamento.PAGO)
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal novoTotalPago = valorPago.add(dto.valor());

        if (novoTotalPago.compareTo(valorPedido) > 0) {
            throw new RuntimeException(
                    "O pagamento ultrapassa o valor total do pedido");
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setValor(dto.valor());
        pagamento.setDataPagamento(
                dto.dataPagamento() != null
                        ? dto.dataPagamento()
                        : java.time.LocalDateTime.now());

        pagamento.setDescricao(dto.descricao());
        pagamento.setStatus(dto.status());
        pagamento.setPedido(pedido);

        if (comprovante != null && !comprovante.isEmpty()) {
            String caminho = comprovanteService.salvar(comprovante);

            pagamento.setCaminhoComprovante(caminho);
            pagamento.setNomeComprovante(
                    comprovante.getOriginalFilename());
        }

        Pagamento salvo = pagamentoRepository.save(pagamento);

        BigDecimal valorEntrada =
                valorPedido.multiply(new BigDecimal("0.30"));

        if (pedido.getStatus() == StatusPedido.PENDENTE
                && novoTotalPago.compareTo(valorEntrada) >= 0) {

            pedido.setStatus(StatusPedido.APROVADO);
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

    boolean possuiComprovante =
            pagamento.getCaminhoComprovante() != null;

    String urlComprovante = possuiComprovante
            ? "/pagamentos/" + pagamento.getId() + "/comprovante"
            : null;

    return new PagamentoResponseDTO(
            pagamento.getId(),
            pagamento.getValor(),
            pagamento.getDataPagamento(),
            pagamento.getDescricao(),
            pagamento.getStatus(),
            pagamento.getPedido().getId(),
            possuiComprovante,
            urlComprovante
    );
}

public Pagamento buscarComprovante(Long pagamentoId) {

    Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
            .orElseThrow(() ->
                    new RuntimeException("Pagamento não encontrado"));

    if (pagamento.getCaminhoComprovante() == null) {
        throw new RuntimeException(
                "Este pagamento não possui comprovante.");
    }

    return pagamento;
}
    public FinanceiroPedidoResponseDTO resumoFinanceiro(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        BigDecimal valorPedido = pedido.getOrcamento().getValor();

        BigDecimal valorEntrada =
                valorPedido.multiply(new BigDecimal("0.30"));

        BigDecimal valorPago = pagamentoRepository
                .findByPedidoId(pedidoId)
                .stream()
                .filter(p -> p.getStatus() == StatusPagamento.PAGO)
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