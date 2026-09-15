package br.edu.ifpr.marcenariapro.dto;

public record ItemProducaoRequestDTO(
        Integer quantidade,
        Long materialId
) {
}