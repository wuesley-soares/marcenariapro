package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{producaoId}/materiais")
    public ResponseEntity<ItemProducaoResponseDTO> salvar(
            @PathVariable Long producaoId,
            @RequestBody ItemProducaoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.salvar(producaoId, dto));
    }

   

@GetMapping("/{producaoId}/materiais")
public ResponseEntity<List<ItemProducaoResponseDTO>> listarPorProducao(
        @PathVariable Long producaoId) {

    return ResponseEntity.ok(
            service.listarPorProducao(producaoId)
    );
}
}