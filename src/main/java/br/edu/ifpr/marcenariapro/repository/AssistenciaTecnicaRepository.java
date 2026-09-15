package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.AssistenciaTecnica;

public interface AssistenciaTecnicaRepository
        extends JpaRepository<AssistenciaTecnica, Long> {
}