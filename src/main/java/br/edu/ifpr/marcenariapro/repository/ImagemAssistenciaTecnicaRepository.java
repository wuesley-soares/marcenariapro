package br.edu.ifpr.marcenariapro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.ImagemAssistenciaTecnica;
import br.edu.ifpr.marcenariapro.enums.EtapaImagemAssistencia;

public interface ImagemAssistenciaTecnicaRepository
        extends JpaRepository<ImagemAssistenciaTecnica, Long> {

    List<ImagemAssistenciaTecnica> findByAssistenciaIdOrderByDataUploadDesc(
            Long assistenciaId
    );

    List<ImagemAssistenciaTecnica>
            findByAssistenciaIdAndEtapaOrderByDataUploadDesc(
                    Long assistenciaId,
                    EtapaImagemAssistencia etapa
            );
}