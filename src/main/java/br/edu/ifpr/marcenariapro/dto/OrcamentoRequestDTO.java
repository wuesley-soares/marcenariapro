package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;

public record OrcamentoRequestDTO(
    BigDecimal valorMaoObra,
    BigDecimal margemLucro,
    String descricao,
    StatusOrcamento status,
    Long ambienteId
) {}