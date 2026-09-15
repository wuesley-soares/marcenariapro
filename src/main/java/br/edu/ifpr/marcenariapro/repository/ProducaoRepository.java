package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Producao;

public interface ProducaoRepository extends JpaRepository<Producao, Long> {
}