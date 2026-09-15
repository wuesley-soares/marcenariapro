package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.ProjetoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProjetoResponseDTO;
import br.edu.ifpr.marcenariapro.service.ProjetoService;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService service;

    public ProjetoController(ProjetoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> salvar(
            @RequestBody ProjetoRequestDTO dto) {

        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }
}