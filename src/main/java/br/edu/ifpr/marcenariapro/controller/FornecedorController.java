package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpr.marcenariapro.dto.FornecedorRequestDTO;
import br.edu.ifpr.marcenariapro.dto.FornecedorResponseDTO;
import br.edu.ifpr.marcenariapro.service.FornecedorService;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> cadastrar(
            @RequestBody FornecedorRequestDTO dto) {

        return ResponseEntity.ok(service.salvar(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody FornecedorRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}