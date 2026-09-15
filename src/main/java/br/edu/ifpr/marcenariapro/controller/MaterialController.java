package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.MaterialRequestDTO;
import br.edu.ifpr.marcenariapro.dto.MaterialResponseDTO;
import br.edu.ifpr.marcenariapro.service.MaterialService;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MaterialResponseDTO> salvar(
            @RequestBody MaterialRequestDTO dto) {

        MaterialResponseDTO material = service.salvar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(material);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<MaterialResponseDTO>> listarEstoqueBaixo() {

    return ResponseEntity.ok(service.listarEstoqueBaixo());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MaterialRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}