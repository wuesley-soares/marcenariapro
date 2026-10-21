package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record ItemProducaoRequestDTO(
        BigDecimal quantidade,
        Long materialId
) {
}