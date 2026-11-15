package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.StatusItemProducao;

public record ItemProducaoResponseDTO(
        Long id,
        BigDecimal quantidade,
        Long materialId,
        String materialNome,
        StatusItemProducao status
) {
}