package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record ItemAssistenciaTecnicaRequestDTO(
        BigDecimal quantidade,
        Long materialId
) {
}