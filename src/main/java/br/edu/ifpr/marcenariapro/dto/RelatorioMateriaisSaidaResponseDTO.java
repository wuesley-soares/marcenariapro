package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record RelatorioMateriaisSaidaResponseDTO(
        Long materialId,
        String material,
        BigDecimal quantidadeSaida
) {}