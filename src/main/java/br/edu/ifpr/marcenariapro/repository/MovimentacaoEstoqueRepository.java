package br.edu.ifpr.marcenariapro.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpr.marcenariapro.entity.MovimentacaoEstoque;

public interface MovimentacaoEstoqueRepository
        extends JpaRepository<MovimentacaoEstoque, Long> {

    boolean existsByMaterialId(Long materialId);

    @Query("""
        SELECT m.id, m.nome, SUM(mov.quantidade)
        FROM MovimentacaoEstoque mov
        JOIN mov.material m
        WHERE mov.tipo = br.edu.ifpr.marcenariapro.enums.TipoMovimentacao.SAIDA
          AND (:inicio IS NULL OR mov.dataMovimentacao >= :inicio)
          AND (:fim IS NULL OR mov.dataMovimentacao < :fim)
        GROUP BY m.id, m.nome
        ORDER BY SUM(mov.quantidade) DESC
        """)
    List<Object[]> buscarMateriaisComMaisSaidas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}