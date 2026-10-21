package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusPedido;

public record PedidoResponseDTO(
    Long id,
    StatusPedido status,
    LocalDateTime dataCriacao,
    LocalDate previsaoEntrega,
    LocalDate dataEntrega,
    Long orcamentoId
) {}