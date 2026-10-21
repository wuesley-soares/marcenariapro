package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.ItemOrcamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemOrcamentoResponseDTO;
import br.edu.ifpr.marcenariapro.service.ItemOrcamentoService;

@RestController
@RequestMapping("/orcamentos")
public class ItemOrcamentoController {

    private final ItemOrcamentoService service;

    public ItemOrcamentoController(
            ItemOrcamentoService service) {

        this.service = service;
    }

    @PostMapping("/{orcamentoId}/materiais")
    public ResponseEntity<ItemOrcamentoResponseDTO> salvar(
            @PathVariable Long orcamentoId,
            @RequestBody ItemOrcamentoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.salvar(orcamentoId, dto));
    }

    @GetMapping("/{orcamentoId}/materiais")
    public ResponseEntity<List<ItemOrcamentoResponseDTO>> listar(
            @PathVariable Long orcamentoId) {

        return ResponseEntity.ok(
                service.listarPorOrcamento(orcamentoId)
        );
    }

    @DeleteMapping("/materiais/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}