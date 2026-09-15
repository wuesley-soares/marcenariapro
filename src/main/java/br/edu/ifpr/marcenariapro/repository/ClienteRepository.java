package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}