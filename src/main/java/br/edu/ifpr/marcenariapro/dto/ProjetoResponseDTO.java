package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjetoResponseDTO(
        Long id,
        String nome,
        String descricao,
        LocalDateTime dataCriacao,
        Long clienteId,
        String clienteNome,
        BigDecimal valorTotal
) {}