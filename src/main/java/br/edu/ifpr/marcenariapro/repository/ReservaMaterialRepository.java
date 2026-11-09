package br.edu.ifpr.marcenariapro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.marcenariapro.entity.ReservaMaterial;
import br.edu.ifpr.marcenariapro.enums.StatusReservaMaterial;

public interface ReservaMaterialRepository
        extends JpaRepository<ReservaMaterial, Long> {

    Optional<ReservaMaterial> findByItemProducaoId(Long itemProducaoId);

    Optional<ReservaMaterial> findByItemAssistenciaTecnicaId(
            Long itemAssistenciaTecnicaId);

    List<ReservaMaterial> findByMaterialIdAndStatus(
            Long materialId,
            StatusReservaMaterial status);

    List<ReservaMaterial> findByStatus(StatusReservaMaterial status);

    boolean existsByItemProducaoId(Long itemProducaoId);

    boolean existsByItemAssistenciaTecnicaId(
            Long itemAssistenciaTecnicaId);
}