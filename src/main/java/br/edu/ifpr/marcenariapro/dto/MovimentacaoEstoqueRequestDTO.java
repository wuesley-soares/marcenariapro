package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;

public record MovimentacaoEstoqueRequestDTO(
    TipoMovimentacao tipo,
    BigDecimal quantidade,
    Long materialId
) {
}