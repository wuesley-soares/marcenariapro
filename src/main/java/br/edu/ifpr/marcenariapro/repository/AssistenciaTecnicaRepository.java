package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.AssistenciaTecnica;
import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;

public interface AssistenciaTecnicaRepository
        extends JpaRepository<AssistenciaTecnica, Long> {

      long countByClienteId(Long clienteId);

    long countByStatus(StatusAssistencia status);
}