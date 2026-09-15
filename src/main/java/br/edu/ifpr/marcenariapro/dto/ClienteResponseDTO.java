package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDateTime;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpfCnpj,
        String endereco,
        LocalDateTime dataCadastro
) {
}