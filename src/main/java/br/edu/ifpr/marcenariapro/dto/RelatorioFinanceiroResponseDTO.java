package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record RelatorioFinanceiroResponseDTO(
        Long pedidoId,
        BigDecimal valorPedido,
        BigDecimal valorPago,
        BigDecimal valorRestante,
        String situacao
) {}