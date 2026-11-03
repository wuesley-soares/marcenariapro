package br.edu.ifpr.marcenariapro.controller;
import br.edu.ifpr.marcenariapro.dto.UsuarioAtualizacaoDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioRequestDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioResponseDTO;
import br.edu.ifpr.marcenariapro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import br.edu.ifpr.marcenariapro.dto.UsuarioSenhaDTO;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UsuarioResponseDTO cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return service.salvar(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listar();
    }

    @PreAuthorize("hasRole('ADMIN')")
@PutMapping("/{id}")
public UsuarioResponseDTO atualizar(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioAtualizacaoDTO dto) {
    return service.atualizar(id, dto);
}

@PreAuthorize("hasRole('ADMIN')")
@PutMapping("/{id}/senha")
public void alterarSenha(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioSenhaDTO dto) {

    service.alterarSenha(id, dto);
}
}