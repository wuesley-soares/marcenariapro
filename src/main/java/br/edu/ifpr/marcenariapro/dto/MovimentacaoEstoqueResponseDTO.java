package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;

public record MovimentacaoEstoqueResponseDTO(
    Long id,
    TipoMovimentacao tipo,
    BigDecimal quantidade,
    LocalDateTime dataMovimentacao,
    Long materialId,
    String materialNome
) {
}