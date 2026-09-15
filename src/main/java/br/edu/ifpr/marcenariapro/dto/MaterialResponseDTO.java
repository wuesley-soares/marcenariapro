package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record MaterialResponseDTO(

    Long id,
    String nome,
    String descricao,
    Integer quantidade,
    Integer estoqueMinimo,
    BigDecimal valorUnitario

) {
}