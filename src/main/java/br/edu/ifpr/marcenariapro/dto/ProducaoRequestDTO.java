package br.edu.ifpr.marcenariapro.dto;

import br.edu.ifpr.marcenariapro.enums.StatusProducao;

public record ProducaoRequestDTO(

    StatusProducao status,
    Long pedidoId

) {
}