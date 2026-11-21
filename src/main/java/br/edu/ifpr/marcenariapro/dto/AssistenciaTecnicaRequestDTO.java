package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;

public record AssistenciaTecnicaRequestDTO(

        LocalDate dataSolicitacao,
        LocalDateTime dataAgendamento,
        String descricao,
        LocalDate dataEntregaOriginal,

        BigDecimal valorServico,
        BigDecimal valorMateriais,

        StatusAssistencia status,

        Long clienteId,
        Long pedidoId,
        Long funcionarioId

) {
}