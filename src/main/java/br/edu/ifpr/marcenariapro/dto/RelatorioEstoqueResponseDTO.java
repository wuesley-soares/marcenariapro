package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.UnidadeMedida;

public record RelatorioEstoqueResponseDTO(
        Long materialId,
        String material,
        BigDecimal quantidadeAtual,
        BigDecimal estoqueMinimo,
        UnidadeMedida unidadeMedida,
        String situacao
) {}