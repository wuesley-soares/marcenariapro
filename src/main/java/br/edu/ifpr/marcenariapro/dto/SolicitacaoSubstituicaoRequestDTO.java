package br.edu.ifpr.marcenariapro.dto;

public record SolicitacaoSubstituicaoRequestDTO(
        Long itemProducaoId,
        Long novoMaterialId
) {
}