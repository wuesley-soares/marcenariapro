package br.edu.ifpr.marcenariapro.dto;

import java.util.List;

public record ProjetoClienteDTO(
    Long id,
    String nome,
    List<AmbienteClienteDTO> ambientes
) {
}