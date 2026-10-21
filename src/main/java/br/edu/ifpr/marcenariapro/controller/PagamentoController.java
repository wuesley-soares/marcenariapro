package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.FinanceiroPedidoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoResponseDTO;
import br.edu.ifpr.marcenariapro.service.PagamentoService;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping
    public PagamentoResponseDTO salvar(
            @RequestBody PagamentoRequestDTO dto) {

        return pagamentoService.salvar(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public List<PagamentoResponseDTO> listar() {

        return pagamentoService.listar();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/pedido/{pedidoId}")
    public List<PagamentoResponseDTO> listarPorPedido(
            @PathVariable Long pedidoId) {

        return pagamentoService.listarPorPedido(pedidoId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/pedido/{pedidoId}/resumo")
    public FinanceiroPedidoResponseDTO resumoFinanceiro(
            @PathVariable Long pedidoId) {

        return pagamentoService.resumoFinanceiro(pedidoId);
    }
}