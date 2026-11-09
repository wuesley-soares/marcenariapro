
package br.edu.ifpr.marcenariapro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusNecessidadeCompra;

public record NecessidadeCompraResponseDTO(
        Long id,
        Long materialId,
        String materialNome,
        BigDecimal quantidadeNecessaria,
        BigDecimal quantidadeAtendida,
        BigDecimal quantidadePendente,
        StatusNecessidadeCompra status,
        LocalDateTime dataCriacao,
        Long itemProducaoId,
        Long itemAssistenciaId
) {
}
