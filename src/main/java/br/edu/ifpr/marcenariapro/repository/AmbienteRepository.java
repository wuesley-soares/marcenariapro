package br.edu.ifpr.marcenariapro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Ambiente;

public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

    long countByProjetoId(Long projetoId);
    List<Ambiente> findByProjetoId(Long projetoId);
}