package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.UnidadeMedida;

public record MaterialResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal quantidade,
    BigDecimal estoqueMinimo,
    BigDecimal valorUnitario,
    UnidadeMedida unidadeMedida
) {
}