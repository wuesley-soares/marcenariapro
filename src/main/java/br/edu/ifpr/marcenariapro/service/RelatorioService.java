package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.RelatorioAssistenciaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioEstoqueResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioFinanceiroResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioPedidosResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioResumoResponseDTO;
import br.edu.ifpr.marcenariapro.enums.StatusProducao;
import br.edu.ifpr.marcenariapro.repository.AssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.ProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;
import br.edu.ifpr.marcenariapro.dto.RelatorioAssistenciaResponseDTO;


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

   public RelatorioResumoResponseDTO gerarResumo() {

    long totalClientes = clienteRepository.count();
    long totalProjetos = projetoRepository.count();
    long totalOrcamentos = orcamentoRepository.count();
    long totalPedidos = pedidoRepository.count();
    long totalAssistencias = assistenciaRepository.count();

    long pedidosEmProducao =
            producaoRepository.countByStatus(StatusProducao.EM_PRODUCAO);

    long pedidosConcluidos =
            producaoRepository.countByStatus(StatusProducao.PRONTO)
            + producaoRepository.countByStatus(StatusProducao.ENTREGUE);

    double valorTotalVendas =
            pedidoRepository.somarValorVendas().doubleValue();

    double valorTotalRecebido =
            pagamentoRepository.somarPagamentosRealizados().doubleValue();

    return new RelatorioResumoResponseDTO(
            totalClientes,
            totalProjetos,
            totalOrcamentos,
            totalPedidos,
            pedidosEmProducao,
            pedidosConcluidos,
            totalAssistencias,
            valorTotalVendas,
            valorTotalRecebido
    );
}

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
                    )
            )
            .toList();
}

public List<RelatorioFinanceiroResponseDTO> financeiro() {

    return pedidoRepository.findAll()
            .stream()
            .map(pedido -> {

                var valorPedido = pedido.getOrcamento().getValor();

                var valorPago = pagamentoRepository
                        .findByPedidoId(pedido.getId())
                        .stream()
                        .filter(pagamento ->
                                pagamento.getStatus() ==
                                br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO)
                        .map(pagamento -> pagamento.getValor())
                        .reduce(java.math.BigDecimal.ZERO,
                                java.math.BigDecimal::add);

                var valorRestante =
                        valorPedido.subtract(valorPago);

                String situacao;

                if (valorPago.compareTo(java.math.BigDecimal.ZERO) == 0) {
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

public List<RelatorioProducaoResponseDTO> producao() {

    return java.util.Arrays.stream(StatusProducao.values())
            .map(status -> new RelatorioProducaoResponseDTO(
                    status.name(),
                    producaoRepository.countByStatus(status)
            ))
            .toList();
}

public List<RelatorioPedidosResponseDTO> pedidosPorStatus() {

    return java.util.Arrays.stream(br.edu.ifpr.marcenariapro.enums.StatusPedido.values())
            .map(status -> new RelatorioPedidosResponseDTO(
                    status.name(),
                    pedidoRepository.countByStatus(status)
            ))
            .toList();
}

public List<RelatorioAssistenciaResponseDTO> assistenciasPorStatus() {

    return java.util.Arrays.stream(
            br.edu.ifpr.marcenariapro.enums.StatusAssistencia.values())
            .map(status -> new RelatorioAssistenciaResponseDTO(
                    status.name(),
                    assistenciaRepository.countByStatus(status)
            ))
            .toList();
}
}