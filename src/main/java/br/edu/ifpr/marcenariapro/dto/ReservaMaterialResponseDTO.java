
package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusReservaMaterial;

public record ReservaMaterialResponseDTO(
        Long id,
        Long materialId,
        String materialNome,
        BigDecimal quantidadeSolicitada,
        BigDecimal quantidadeReservada,
        BigDecimal quantidadeConsumida,
        StatusReservaMaterial status,
        LocalDateTime dataCriacao,
        Long itemProducaoId,
        Long itemAssistenciaId
) {
}
