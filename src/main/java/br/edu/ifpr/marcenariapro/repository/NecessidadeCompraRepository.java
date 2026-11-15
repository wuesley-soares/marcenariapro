package br.edu.ifpr.marcenariapro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.NecessidadeCompra;
import br.edu.ifpr.marcenariapro.enums.StatusNecessidadeCompra;

public interface NecessidadeCompraRepository
        extends JpaRepository<NecessidadeCompra, Long> {

    Optional<NecessidadeCompra> findByItemProducaoId(Long itemProducaoId);

    Optional<NecessidadeCompra> findByItemAssistenciaTecnicaId(
            Long itemAssistenciaTecnicaId);

    List<NecessidadeCompra> findByStatus(
            StatusNecessidadeCompra status);

    List<NecessidadeCompra> findByMaterialIdAndStatus(
            Long materialId,
            StatusNecessidadeCompra status);

    List<NecessidadeCompra> findByMaterialIdAndStatusIn(
            Long materialId,
            List<StatusNecessidadeCompra> status);

    boolean existsByItemProducaoIdAndStatus(
            Long itemProducaoId,
            StatusNecessidadeCompra status);

    boolean existsByItemAssistenciaTecnicaIdAndStatus(
            Long itemAssistenciaTecnicaId,
            StatusNecessidadeCompra status);
}