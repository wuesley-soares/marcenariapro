package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;

public record OrcamentoResponseDTO(

    Long id,

    BigDecimal valor,

    BigDecimal valorMaoObra,

    BigDecimal margemLucro,

    String descricao,

    StatusOrcamento status,

    LocalDateTime dataCriacao,

    LocalDateTime dataValidade,

    Long ambienteId,

    String ambienteNome,

    String projetoNome

) {}