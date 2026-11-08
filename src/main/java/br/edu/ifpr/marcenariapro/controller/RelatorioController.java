package br.edu.ifpr.marcenariapro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    private void validarDatas(LocalDate inicio, LocalDate fim) {
        if ((inicio == null) != (fim == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe a data inicial e a data final.");
        }

        if (inicio != null && inicio.isAfter(fim)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data inicial não pode ser posterior à data final.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/resumo")
    public RelatorioResumoResponseDTO resumo(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        validarDatas(inicio, fim);
        return relatorioService.gerarResumo(inicio, fim);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/estoque")
    public List<RelatorioEstoqueResponseDTO> estoqueBaixo() {
        return relatorioService.estoqueBaixo();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/financeiro")
    public List<RelatorioFinanceiroResponseDTO> financeiro(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        validarDatas(inicio, fim);
        return relatorioService.financeiro(inicio, fim);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/producao")
    public List<RelatorioProducaoResponseDTO> producao(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        validarDatas(inicio, fim);
        return relatorioService.producao(inicio, fim);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/pedidos-status")
    public List<RelatorioPedidosResponseDTO> pedidosPorStatus(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        validarDatas(inicio, fim);
        return relatorioService.pedidosPorStatus(inicio, fim);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/assistencias-status")
    public List<RelatorioAssistenciaResponseDTO> assistenciasPorStatus(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        validarDatas(inicio, fim);
        return relatorioService.assistenciasPorStatus(inicio, fim);
    }
}