package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpr.marcenariapro.dto.ItemProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.service.ItemProducaoService;

@RestController
@RequestMapping("/producoes")
public class ItemProducaoController {

    private final ItemProducaoService service;

    public ItemProducaoController(ItemProducaoService service) {
        this.service = service;
    }

    // Adicionar material à produção
    @PostMapping("/{producaoId}/materiais")
    public ResponseEntity<ItemProducaoResponseDTO> salvar(
            @PathVariable Long producaoId,
            @RequestBody ItemProducaoRequestDTO dto) {

        ItemProducaoResponseDTO item = service.salvar(producaoId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(item);
    }

    // Listar materiais da produção
    @GetMapping("/{producaoId}/materiais")
    public ResponseEntity<List<ItemProducaoResponseDTO>> listarPorProducao(
            @PathVariable Long producaoId) {

        return ResponseEntity.ok(
                service.listarPorProducao(producaoId)
        );
    }

    // Remover material da produção
    @DeleteMapping("/{producaoId}/materiais/{itemId}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long producaoId,
            @PathVariable Long itemId) {

        service.excluir(producaoId, itemId);

        return ResponseEntity.noContent().build();
    }
}