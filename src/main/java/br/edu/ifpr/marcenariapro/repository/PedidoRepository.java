package br.edu.ifpr.marcenariapro.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    long countByStatus(StatusPedido status);

    Optional<Pedido> findByOrcamentoId(Long orcamentoId);

    @Query("""
        SELECT COALESCE(SUM(p.orcamento.valor), 0)
        FROM Pedido p
        """)
    BigDecimal somarValorVendas();
}