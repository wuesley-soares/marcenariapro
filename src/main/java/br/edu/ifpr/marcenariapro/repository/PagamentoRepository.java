package br.edu.ifpr.marcenariapro.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.enums.StatusPagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPedidoId(Long pedidoId);

    List<Pagamento> findByPedidoIdAndDataPagamentoGreaterThanEqualAndDataPagamentoLessThan(
            Long pedidoId, LocalDateTime inicio, LocalDateTime fim);

    long countByStatus(StatusPagamento status);

    @Query("""
        SELECT COALESCE(SUM(p.valor), 0)
        FROM Pagamento p
        WHERE p.status = br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO
        """)
    BigDecimal somarPagamentosRealizados();

    @Query("""
        SELECT COALESCE(SUM(p.valor), 0)
        FROM Pagamento p
        WHERE p.status = br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO
        AND p.dataPagamento >= :inicio
        AND p.dataPagamento < :fim
        """)
    BigDecimal somarPagamentosPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

            @Query("""
    SELECT YEAR(p.dataPagamento), MONTH(p.dataPagamento),
           COALESCE(SUM(p.valor), 0)
    FROM Pagamento p
    WHERE p.status = br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO
      AND p.dataPagamento IS NOT NULL
      AND (:inicio IS NULL OR p.dataPagamento >= :inicio)
      AND (:fim IS NULL OR p.dataPagamento < :fim)
    GROUP BY YEAR(p.dataPagamento), MONTH(p.dataPagamento)
    ORDER BY YEAR(p.dataPagamento), MONTH(p.dataPagamento)
    """)
List<Object[]> buscarRecebimentosMensais(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim);
}