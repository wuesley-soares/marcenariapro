package br.edu.ifpr.marcenariapro.dto;

import br.edu.ifpr.marcenariapro.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioAtualizacaoDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotNull(message = "O perfil é obrigatório")
        PerfilUsuario perfil,

        @NotNull(message = "A situação é obrigatória")
        Boolean ativo
) {}