package br.edu.ifpr.marcenariapro.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.Producao;
import br.edu.ifpr.marcenariapro.enums.StatusProducao;

public interface ProducaoRepository extends JpaRepository<Producao, Long> {

    long countByStatus(StatusProducao status);

    long countByStatusAndDataCriacaoGreaterThanEqualAndDataCriacaoLessThan(
            StatusProducao status, LocalDateTime inicio, LocalDateTime fim);

    boolean existsByPedidoId(Long pedidoId);
}