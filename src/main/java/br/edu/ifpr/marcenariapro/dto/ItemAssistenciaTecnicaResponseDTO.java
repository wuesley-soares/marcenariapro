package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record ItemAssistenciaTecnicaResponseDTO(
        Long id,
        BigDecimal quantidade,
        Long materialId,
        String materialNome
) {
}