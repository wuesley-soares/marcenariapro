package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record FinanceiroClienteDTO(
    BigDecimal totalPedidos,
    BigDecimal totalPago,
    BigDecimal saldoReceber,
    Long quantidadePedidos,
    String situacao
) {
}