package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record MaterialRequestDTO(

    String nome,
    String descricao,
    Integer quantidade,
    Integer estoqueMinimo,
    BigDecimal valorUnitario
    

) {
}