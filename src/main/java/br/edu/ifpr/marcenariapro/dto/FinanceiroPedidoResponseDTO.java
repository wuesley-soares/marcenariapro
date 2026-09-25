package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record FinanceiroPedidoResponseDTO(
        Long pedidoId,
        BigDecimal valorPedido,
        BigDecimal valorEntrada,
        BigDecimal valorPago,
        BigDecimal valorRestante,
        String situacao
) {}