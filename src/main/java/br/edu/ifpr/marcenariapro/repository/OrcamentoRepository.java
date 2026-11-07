package br.edu.ifpr.marcenariapro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpr.marcenariapro.entity.Orcamento;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    Optional<Orcamento> findByAmbienteId(Long ambienteId);
    long countByAmbienteId(Long ambienteId);
   

}