package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

public record RelatorioMensalResponseDTO(
        int ano,
        int mes,
        BigDecimal valor
) {}