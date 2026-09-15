package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDateTime;

public record AmbienteResponseDTO(

    Long id,
    String nome,
    String descricao,
    LocalDateTime dataCriacao,
    Long projetoId,
    String projetoNome

) {
}