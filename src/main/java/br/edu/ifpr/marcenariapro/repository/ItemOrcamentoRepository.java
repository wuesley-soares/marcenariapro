package br.edu.ifpr.marcenariapro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.ItemOrcamento;

public interface ItemOrcamentoRepository
        extends JpaRepository<ItemOrcamento, Long> {

    List<ItemOrcamento> findByOrcamentoId(Long orcamentoId);

    Optional<ItemOrcamento> findByOrcamentoIdAndMaterialId(
            Long orcamentoId,
            Long materialId
    );
}   