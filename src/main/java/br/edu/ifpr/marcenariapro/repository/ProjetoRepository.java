package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Projeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

}