package br.edu.ifpr.marcenariapro.dto;

import br.edu.ifpr.marcenariapro.enums.StatusPedido;

public record PedidoRequestDTO(

    StatusPedido status,
    Long orcamentoId

) {
}