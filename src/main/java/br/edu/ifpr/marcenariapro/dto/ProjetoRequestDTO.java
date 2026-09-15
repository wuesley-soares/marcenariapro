package br.edu.ifpr.marcenariapro.dto;

public record ProjetoRequestDTO(

    String nome,
    String descricao,
    Long clienteId

) {
}