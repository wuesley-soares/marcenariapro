package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record ItemOrcamentoRequestDTO(

    BigDecimal quantidade,
    String unidade,
    BigDecimal valorUnitario,
    Long materialId

) {
}