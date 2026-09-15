package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;

public record AssistenciaTecnicaRequestDTO(

        LocalDate dataSolicitacao,
        LocalDateTime dataAgendamento,
        String descricao,
        LocalDate dataEntregaOriginal,

        Double valorServico,
        Double valorMateriais,

        StatusAssistencia status,

        Long clienteId,
        Long pedidoId,
        Long funcionarioId

) {
}