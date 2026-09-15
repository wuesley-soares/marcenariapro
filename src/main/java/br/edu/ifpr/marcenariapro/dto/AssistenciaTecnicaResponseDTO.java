package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;

public record AssistenciaTecnicaResponseDTO(
        Long id,
        LocalDate dataSolicitacao,
        LocalDateTime dataAgendamento,
        String descricao,
        LocalDate dataEntregaOriginal,
        Boolean dentroGarantia,
        Double valorServico,
        Double valorMateriais,
        Double valorTotal,
        StatusAssistencia status,
        Long clienteId,
        Long pedidoId,
        Long funcionarioId
) {
}