package br.edu.ifpr.marcenariapro.dto;

import br.edu.ifpr.marcenariapro.enums.PerfilUsuario;

public record UsuarioResponseDTO(

        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        Boolean ativo

) {
}