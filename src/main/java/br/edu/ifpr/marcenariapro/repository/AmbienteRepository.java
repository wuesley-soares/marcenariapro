package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Ambiente;

public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

}