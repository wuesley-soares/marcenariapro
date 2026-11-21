package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.ProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.service.ProducaoService;

@RestController
@RequestMapping("/producoes")
public class ProducaoController {

    private final ProducaoService service;

    public ProducaoController(ProducaoService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MARCENEIRO')")
    @PostMapping
    public ResponseEntity<ProducaoResponseDTO> salvar(
            @RequestBody ProducaoRequestDTO dto) {

        ProducaoResponseDTO producao = service.salvar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(producao);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping
    public ResponseEntity<List<ProducaoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}")
    public ResponseEntity<ProducaoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MARCENEIRO')")
    @PutMapping("/{id}")
    public ResponseEntity<ProducaoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProducaoRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> excluir(@PathVariable Long id) {

    service.excluir(id);

    return ResponseEntity.noContent().build();
}
}