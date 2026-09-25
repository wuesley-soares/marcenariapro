package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

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

    @PostMapping
    public PagamentoResponseDTO salvar(
            @RequestBody PagamentoRequestDTO dto) {

        return pagamentoService.salvar(dto);
    }

    @GetMapping
    public List<PagamentoResponseDTO> listar() {
        return pagamentoService.listar();
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<PagamentoResponseDTO> listarPorPedido(
            @PathVariable Long pedidoId) {

        return pagamentoService.listarPorPedido(pedidoId);
    }

    @GetMapping("/pedido/{pedidoId}/resumo")
    public FinanceiroPedidoResponseDTO resumoFinanceiro(
        @PathVariable Long pedidoId) {

        return pagamentoService.resumoFinanceiro(pedidoId);
    }
}