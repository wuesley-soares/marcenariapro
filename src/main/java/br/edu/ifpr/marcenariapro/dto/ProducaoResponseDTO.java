package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusProducao;

public record ProducaoResponseDTO(

    Long id,
    StatusProducao status,
    LocalDateTime dataCriacao,
    Long pedidoId

) {
}