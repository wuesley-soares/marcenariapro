
package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.ReservaMaterialResponseDTO;
import br.edu.ifpr.marcenariapro.entity.ReservaMaterial;
import br.edu.ifpr.marcenariapro.enums.StatusReservaMaterial;
import br.edu.ifpr.marcenariapro.repository.ReservaMaterialRepository;

@RestController
@RequestMapping("/reservas-materiais")
public class ReservaMaterialController {

    private final ReservaMaterialRepository repository;

    public ReservaMaterialController(
            ReservaMaterialRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @Transactional(readOnly = true)
    public List<ReservaMaterialResponseDTO> listar(
            @RequestParam(required = false) StatusReservaMaterial status,
            @RequestParam(required = false) Long materialId) {

        return repository.findAll()
                .stream()
                .filter(reserva ->
                        status == null || reserva.getStatus() == status)
                .filter(reserva ->
                        materialId == null
                                || reserva.getMaterial().getId().equals(materialId))
                .sorted((a, b) ->
                        b.getDataCriacao().compareTo(a.getDataCriacao()))
                .map(this::converterParaResponse)
                .toList();
    }

    private ReservaMaterialResponseDTO converterParaResponse(
            ReservaMaterial reserva) {

        return new ReservaMaterialResponseDTO(
                reserva.getId(),
                reserva.getMaterial().getId(),
                reserva.getMaterial().getNome(),
                reserva.getQuantidadeSolicitada(),
                reserva.getQuantidadeReservada(),
                reserva.getQuantidadeConsumida(),
                reserva.getStatus(),
                reserva.getDataCriacao(),
                reserva.getItemProducao() != null
                        ? reserva.getItemProducao().getId() : null,
                reserva.getItemAssistenciaTecnica() != null
                        ? reserva.getItemAssistenciaTecnica().getId() : null
        );
    }
}
