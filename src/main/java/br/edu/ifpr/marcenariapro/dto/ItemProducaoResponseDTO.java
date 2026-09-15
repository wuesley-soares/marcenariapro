package br.edu.ifpr.marcenariapro.dto;

public record ItemProducaoResponseDTO(
        Long id,
        Integer quantidade,
        Long materialId,
        String materialNome
) {
}