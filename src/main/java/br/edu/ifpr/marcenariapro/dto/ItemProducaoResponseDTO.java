package br.edu.ifpr.marcenariapro.dto;
import java.math.BigDecimal;

public record ItemProducaoResponseDTO(
        Long id,
        BigDecimal quantidade,
        Long materialId,
        String materialNome
) {
}