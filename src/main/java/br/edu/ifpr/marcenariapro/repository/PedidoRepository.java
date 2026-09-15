package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}