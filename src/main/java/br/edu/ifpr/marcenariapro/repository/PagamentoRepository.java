package br.edu.ifpr.marcenariapro.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.enums.StatusPagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPedidoId(Long pedidoId);

    long countByStatus(StatusPagamento status);

    @Query("""
        SELECT COALESCE(SUM(p.valor), 0)
        FROM Pagamento p
        WHERE p.status = br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO
        """)
    BigDecimal somarPagamentosRealizados();
}