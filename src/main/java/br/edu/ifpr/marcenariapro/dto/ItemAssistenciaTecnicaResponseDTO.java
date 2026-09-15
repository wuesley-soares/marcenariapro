package br.edu.ifpr.marcenariapro.dto;

public record ItemAssistenciaTecnicaResponseDTO(
        Long id,
        Integer quantidade,
        Long materialId,
        String materialNome
) {
}