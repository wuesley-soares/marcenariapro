package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.RelatorioAssistenciaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioEstoqueResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioFinanceiroResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioPedidosResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.RelatorioResumoResponseDTO;
import br.edu.ifpr.marcenariapro.service.RelatorioService;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/resumo")
    public RelatorioResumoResponseDTO resumo() {
        return relatorioService.gerarResumo();
    }

    @GetMapping("/estoque")
    public List<RelatorioEstoqueResponseDTO> estoqueBaixo() {
        return relatorioService.estoqueBaixo();
    }

    @GetMapping("/financeiro")
    public List<RelatorioFinanceiroResponseDTO> financeiro() {
        return relatorioService.financeiro();
    }

    @GetMapping("/producao")
    public List<RelatorioProducaoResponseDTO> producao() {
    return relatorioService.producao();
    }   

    @GetMapping("/pedidos-status")
    public List<RelatorioPedidosResponseDTO> pedidosPorStatus() {
    return relatorioService.pedidosPorStatus();
}

    @GetMapping("/assistencias-status")
    public List<RelatorioAssistenciaResponseDTO> assistenciasPorStatus() {
    return relatorioService.assistenciasPorStatus();
}
}