package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.ItemProducao;

public interface ItemProducaoRepository
        extends JpaRepository<ItemProducao, Long> {

    boolean existsByMaterialId(Long materialId);
}