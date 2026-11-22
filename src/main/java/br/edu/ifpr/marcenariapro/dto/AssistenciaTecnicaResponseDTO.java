
package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;

public record AssistenciaTecnicaResponseDTO(
        Long id,
        LocalDate dataSolicitacao,
        LocalDateTime dataAgendamento,
        String descricao,
        LocalDate dataEntregaOriginal,
        Boolean dentroGarantia,
        BigDecimal valorServico,
        BigDecimal valorMateriais,
        BigDecimal valorTotal,
        StatusAssistencia status,
        Long clienteId,
        Long pedidoId
) {
}
