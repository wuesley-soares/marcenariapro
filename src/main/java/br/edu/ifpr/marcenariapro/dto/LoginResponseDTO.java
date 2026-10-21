package br.edu.ifpr.marcenariapro.dto;

public record LoginResponseDTO(

        String mensagem,
        Long usuarioId,
        String nome,
        String email,
        String perfil,
        String token

) {
}