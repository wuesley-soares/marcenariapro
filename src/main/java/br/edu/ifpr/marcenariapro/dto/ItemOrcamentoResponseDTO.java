package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record ItemOrcamentoResponseDTO(

    Long id,
    BigDecimal quantidade,
    String unidade,
    BigDecimal valorUnitario,
    BigDecimal subtotal,
    Long materialId,
    String materialNome

) {
}