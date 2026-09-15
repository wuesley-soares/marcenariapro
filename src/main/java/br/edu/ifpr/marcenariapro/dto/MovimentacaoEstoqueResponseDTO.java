package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;

public record MovimentacaoEstoqueResponseDTO(

    Long id,
    TipoMovimentacao tipo,
    Integer quantidade,
    LocalDateTime dataMovimentacao,
    Long materialId,
    String materialNome

) {
}