package br.edu.ifpr.marcenariapro.dto;

import java.util.List;

public record ClienteDetalheDTO(
    ClienteResponseDTO cliente,
    FinanceiroClienteDTO financeiro,
    List<ProjetoClienteDTO> projetos
) {
}