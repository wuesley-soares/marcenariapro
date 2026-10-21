package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/resumo")
    public RelatorioResumoResponseDTO resumo() {
        return relatorioService.gerarResumo();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/estoque")
    public List<RelatorioEstoqueResponseDTO> estoqueBaixo() {
        return relatorioService.estoqueBaixo();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/financeiro")
    public List<RelatorioFinanceiroResponseDTO> financeiro() {
        return relatorioService.financeiro();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/producao")
    public List<RelatorioProducaoResponseDTO> producao() {
        return relatorioService.producao();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/pedidos-status")
    public List<RelatorioPedidosResponseDTO> pedidosPorStatus() {
        return relatorioService.pedidosPorStatus();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/assistencias-status")
    public List<RelatorioAssistenciaResponseDTO> assistenciasPorStatus() {
        return relatorioService.assistenciasPorStatus();
    }
}