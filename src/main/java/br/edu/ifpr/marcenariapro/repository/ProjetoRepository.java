package br.edu.ifpr.marcenariapro.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import br.edu.ifpr.marcenariapro.entity.Projeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    long countByClienteId(Long clienteId);

    @Query("""
        SELECT COALESCE(SUM(o.valor), 0)
        FROM Orcamento o
        WHERE o.ambiente.projeto.id = :projetoId
    """)
    BigDecimal somarOrcamentosPorProjeto(
            @Param("projetoId") Long projetoId);

            List<Projeto> findByClienteId(Long clienteId);
}