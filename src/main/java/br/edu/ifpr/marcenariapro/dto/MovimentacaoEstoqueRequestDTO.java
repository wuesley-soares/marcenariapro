package br.edu.ifpr.marcenariapro.dto;

import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;

public record MovimentacaoEstoqueRequestDTO(

    TipoMovimentacao tipo,
    Integer quantidade,
    Long materialId

) {
}