
package br.edu.ifpr.marcenariapro.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.NecessidadeCompraResponseDTO;
import br.edu.ifpr.marcenariapro.entity.NecessidadeCompra;
import br.edu.ifpr.marcenariapro.enums.StatusNecessidadeCompra;
import br.edu.ifpr.marcenariapro.repository.NecessidadeCompraRepository;

@RestController
@RequestMapping("/necessidades-compra")
public class NecessidadeCompraController {

    private final NecessidadeCompraRepository repository;

    public NecessidadeCompraController(
            NecessidadeCompraRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @Transactional(readOnly = true)
    public List<NecessidadeCompraResponseDTO> listar(
            @RequestParam(required = false) StatusNecessidadeCompra status,
            @RequestParam(required = false) Long materialId) {

        return repository.findAll()
                .stream()
                .filter(necessidade ->
                        status == null || necessidade.getStatus() == status)
                .filter(necessidade ->
                        materialId == null
                                || necessidade.getMaterial().getId().equals(materialId))
                .sorted((a, b) ->
                        b.getDataCriacao().compareTo(a.getDataCriacao()))
                .map(this::converterParaResponse)
                .toList();
    }

    private NecessidadeCompraResponseDTO converterParaResponse(
            NecessidadeCompra necessidade) {

        BigDecimal quantidadePendente =
                necessidade.getQuantidadeNecessaria()
                        .subtract(necessidade.getQuantidadeAtendida());

        return new NecessidadeCompraResponseDTO(
                necessidade.getId(),
                necessidade.getMaterial().getId(),
                necessidade.getMaterial().getNome(),
                necessidade.getQuantidadeNecessaria(),
                necessidade.getQuantidadeAtendida(),
                quantidadePendente,
                necessidade.getStatus(),
                necessidade.getDataCriacao(),
                necessidade.getItemProducao() != null
                        ? necessidade.getItemProducao().getId() : null,
                necessidade.getItemAssistenciaTecnica() != null
                        ? necessidade.getItemAssistenciaTecnica().getId() : null
        );
    }
}
