package br.edu.ifpr.marcenariapro.dto;

public record RelatorioEstoqueResponseDTO(
        Long materialId,
        String material,
        Integer quantidadeAtual,
        Integer estoqueMinimo,
        String situacao
) {}