package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusPagamento;

public record PagamentoResponseDTO(
        Long id,
        BigDecimal valor,
        LocalDateTime dataPagamento,
        String descricao,
        StatusPagamento status,
        Long pedidoId
) {}