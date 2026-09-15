package br.edu.ifpr.marcenariapro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.ItemAssistenciaTecnica;

public interface ItemAssistenciaTecnicaRepository
        extends JpaRepository<ItemAssistenciaTecnica, Long> {

    List<ItemAssistenciaTecnica> findByAssistenciaId(Long assistenciaId);
}