package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record AmbienteClienteDTO(
    Long id,
    String nome,
    BigDecimal valorOrcamento,
    String statusOrcamento,
    Long pedidoId,
    String statusPedido,
    BigDecimal valorPago,
    BigDecimal saldoRestante,
    String situacaoFinanceira
) {
}