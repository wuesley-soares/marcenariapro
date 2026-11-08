package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.RelatorioAssistenciaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioEstoqueResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioFinanceiroResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioPedidosResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioResumoResponseDTO;
import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;
import br.edu.ifpr.marcenariapro.enums.StatusPagamento;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;
import br.edu.ifpr.marcenariapro.enums.StatusProducao;
import br.edu.ifpr.marcenariapro.repository.AssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.ProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;

@Service
public class RelatorioService {

    private final ClienteRepository clienteRepository;
    private final ProjetoRepository projetoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final AssistenciaTecnicaRepository assistenciaRepository;
    private final ProducaoRepository producaoRepository;
    private final MaterialRepository materialRepository;
    private final PagamentoRepository pagamentoRepository;

    public RelatorioService(
            ClienteRepository clienteRepository,
            ProjetoRepository projetoRepository,
            OrcamentoRepository orcamentoRepository,
            PedidoRepository pedidoRepository,
            ProducaoRepository producaoRepository,
            AssistenciaTecnicaRepository assistenciaTecnicaRepository,
            MaterialRepository materialRepository,
            PagamentoRepository pagamentoRepository) {

        this.clienteRepository = clienteRepository;
        this.projetoRepository = projetoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.producaoRepository = producaoRepository;
        this.assistenciaRepository = assistenciaTecnicaRepository;
        this.materialRepository = materialRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if ((inicio == null) != (fim == null)) {
            throw new IllegalArgumentException(
                    "Informe a data inicial e a data final.");
        }

        if (inicio != null && inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final.");
        }
    }

    private LocalDateTime inicioDoDia(LocalDate data) {
        return data.atStartOfDay();
    }

    private LocalDateTime inicioDoDiaSeguinte(LocalDate data) {
        return data.plusDays(1).atStartOfDay();
    }

    // RESUMO

    public RelatorioResumoResponseDTO gerarResumo() {
        return gerarResumo(null, null);
    }

    public RelatorioResumoResponseDTO gerarResumo(
            LocalDate inicio, LocalDate fim) {

        validarPeriodo(inicio, fim);

        boolean filtrar = inicio != null;

        long totalClientes = clienteRepository.count();
        long totalProjetos = projetoRepository.count();
        long totalOrcamentos = orcamentoRepository.count();

        long totalPedidos = filtrar
                ? pedidoRepository
                    .countByDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                            inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                : pedidoRepository.count();

        long totalAssistencias = 0;

        for (StatusAssistencia status : StatusAssistencia.values()) {
            totalAssistencias += filtrar
                    ? assistenciaRepository.countByStatusAndDataSolicitacaoBetween(
                            status, inicio, fim)
                    : assistenciaRepository.countByStatus(status);
        }

        long pedidosEmProducao = filtrar
                ? producaoRepository
                    .countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                            StatusProducao.EM_PRODUCAO,
                            inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                : producaoRepository.countByStatus(StatusProducao.EM_PRODUCAO);

        long pedidosConcluidos = filtrar
                ? producaoRepository
                    .countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                            StatusProducao.PRONTO,
                            inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                    + producaoRepository
                    .countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                            StatusProducao.ENTREGUE,
                            inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                : producaoRepository.countByStatus(StatusProducao.PRONTO)
                    + producaoRepository.countByStatus(StatusProducao.ENTREGUE);

        BigDecimal vendas = filtrar
                ? pedidoRepository.somarValorVendasPorPeriodo(
                        inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                : pedidoRepository.somarValorVendas();

        BigDecimal recebimentos = filtrar
                ? pagamentoRepository.somarPagamentosPorPeriodo(
                        inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                : pagamentoRepository.somarPagamentosRealizados();

        return new RelatorioResumoResponseDTO(
                totalClientes,
                totalProjetos,
                totalOrcamentos,
                totalPedidos,
                pedidosEmProducao,
                pedidosConcluidos,
                totalAssistencias,
                vendas.doubleValue(),
                recebimentos.doubleValue()
        );
    }

    // ESTOQUE

    public List<RelatorioEstoqueResponseDTO> estoqueBaixo() {

        return materialRepository.findAll()
                .stream()
                .filter(material ->
                        material.getQuantidade() <= material.getEstoqueMinimo())
                .map(material ->
                        new RelatorioEstoqueResponseDTO(
                                material.getId(),
                                material.getNome(),
                                material.getQuantidade(),
                                material.getEstoqueMinimo(),
                                "ESTOQUE BAIXO"
                        ))
                .toList();
    }

    // FINANCEIRO

    public List<RelatorioFinanceiroResponseDTO> financeiro() {
        return financeiro(null, null);
    }

    public List<RelatorioFinanceiroResponseDTO> financeiro(
            LocalDate inicio, LocalDate fim) {

        validarPeriodo(inicio, fim);

        boolean filtrar = inicio != null;

        var pedidos = filtrar
                ? pedidoRepository
                    .findAllByDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                            inicioDoDia(inicio), inicioDoDiaSeguinte(fim))
                : pedidoRepository.findAll();

        return pedidos.stream()
                .map(pedido -> {

                    BigDecimal valorPedido = pedido.getOrcamento().getValor();

                    var pagamentos = filtrar
                            ? pagamentoRepository
                                .findByPedidoIdAndDataPagamentoGreaterThanEqualAndDataPagamentoLessThan(
                                        pedido.getId(),
                                        inicioDoDia(inicio),
                                        inicioDoDiaSeguinte(fim))
                            : pagamentoRepository.findByPedidoId(pedido.getId());

                    BigDecimal valorPago = pagamentos.stream()
                            .filter(pagamento ->
                                    pagamento.getStatus() == StatusPagamento.PAGO)
                            .map(pagamento -> pagamento.getValor())
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

                    return new RelatorioFinanceiroResponseDTO(
                            pedido.getId(),
                            valorPedido,
                            valorPago,
                            valorRestante,
                            situacao
                    );
                })
                .toList();
    }

    // PRODUÇÃO

    public List<RelatorioProducaoResponseDTO> producao() {
        return producao(null, null);
    }

    public List<RelatorioProducaoResponseDTO> producao(
            LocalDate inicio, LocalDate fim) {

        validarPeriodo(inicio, fim);

        return Arrays.stream(StatusProducao.values())
                .map(status -> new RelatorioProducaoResponseDTO(
                        status.name(),
                        inicio == null
                                ? producaoRepository.countByStatus(status)
                                : producaoRepository
                                    .countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                                            status,
                                            inicioDoDia(inicio),
                                            inicioDoDiaSeguinte(fim))
                ))
                .toList();
    }

    // PEDIDOS POR STATUS

    public List<RelatorioPedidosResponseDTO> pedidosPorStatus() {
        return pedidosPorStatus(null, null);
    }

    public List<RelatorioPedidosResponseDTO> pedidosPorStatus(
            LocalDate inicio, LocalDate fim) {

        validarPeriodo(inicio, fim);

        return Arrays.stream(StatusPedido.values())
                .map(status -> new RelatorioPedidosResponseDTO(
                        status.name(),
                        inicio == null
                                ? pedidoRepository.countByStatus(status)
                                : pedidoRepository
                                    .countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
                                            status,
                                            inicioDoDia(inicio),
                                            inicioDoDiaSeguinte(fim))
                ))
                .toList();
    }

    // ASSISTÊNCIAS

    public List<RelatorioAssistenciaResponseDTO> assistenciasPorStatus() {
        return assistenciasPorStatus(null, null);
    }

    public List<RelatorioAssistenciaResponseDTO> assistenciasPorStatus(
            LocalDate inicio, LocalDate fim) {

        validarPeriodo(inicio, fim);

        return Arrays.stream(StatusAssistencia.values())
                .map(status -> new RelatorioAssistenciaResponseDTO(
                        status.name(),
                        inicio == null
                                ? assistenciaRepository.countByStatus(status)
                                : assistenciaRepository
                                    .countByStatusAndDataSolicitacaoBetween(
                                            status, inicio, fim)
                ))
                .toList();
    }
}