package br.edu.ifpr.marcenariapro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.ItemAssistencia;

public interface ItemAssistenciaRepository
        extends JpaRepository<ItemAssistencia, Long> {

    List<ItemAssistencia> findByAssistenciaId(Long assistenciaId);
}