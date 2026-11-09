package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.EtapaImagemAssistencia;

public record ImagemAssistenciaTecnicaResponseDTO(
        Long id,
        String nomeOriginal,
        String tipoConteudo,
        EtapaImagemAssistencia etapa,
        LocalDateTime dataUpload
) {
}