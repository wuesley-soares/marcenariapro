package br.edu.ifpr.marcenariapro.dto;

import br.edu.ifpr.marcenariapro.enums.StatusSubstituicao;

public record SolicitacaoSubstituicaoResponseDTO(
        Long id,
        Long itemProducaoId,
        Long materialAtualId,
        String materialAtualNome,
        Long novoMaterialId,
        String novoMaterialNome,
        StatusSubstituicao status
) {
}