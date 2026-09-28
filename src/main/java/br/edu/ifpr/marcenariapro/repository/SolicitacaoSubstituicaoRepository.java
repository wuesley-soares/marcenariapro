package br.edu.ifpr.marcenariapro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.SolicitacaoSubstituicao;

public interface SolicitacaoSubstituicaoRepository
        extends JpaRepository<SolicitacaoSubstituicao, Long> {

    boolean existsByNovoMaterialId(Long materialId);
}