package br.edu.ifpr.marcenariapro.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    long countByStatus(StatusPedido status);

    Optional<Pedido> findByOrcamentoId(Long orcamentoId);

    List<Pedido> findAllByDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
            LocalDateTime inicio, LocalDateTime fim);

    long countByDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
            LocalDateTime inicio, LocalDateTime fim);

    long countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
            StatusPedido status, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT COALESCE(SUM(p.orcamento.valor), 0)
        FROM Pedido p
        """)
    BigDecimal somarValorVendas();

    @Query("""
        SELECT COALESCE(SUM(p.orcamento.valor), 0)
        FROM Pedido p
        WHERE p.dataCriacao >= :inicio
        AND p.dataCriacao < :fim
        """)
    BigDecimal somarValorVendasPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}