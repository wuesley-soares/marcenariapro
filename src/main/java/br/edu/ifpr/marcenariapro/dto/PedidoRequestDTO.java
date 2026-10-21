package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDate;

import br.edu.ifpr.marcenariapro.enums.StatusPedido;

public record PedidoRequestDTO(

    StatusPedido status,
    LocalDate previsaoEntrega,
    Long orcamentoId

) {
}