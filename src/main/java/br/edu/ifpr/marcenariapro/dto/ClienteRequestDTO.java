package br.edu.ifpr.marcenariapro.dto;

public record ClienteRequestDTO(
        String nome,
        String email,
        String telefone,
        String cpfCnpj,
        String endereco
) {
}