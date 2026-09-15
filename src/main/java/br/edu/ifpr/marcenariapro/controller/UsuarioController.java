package br.edu.ifpr.marcenariapro.controller;

import br.edu.ifpr.marcenariapro.dto.UsuarioRequestDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioResponseDTO;
import br.edu.ifpr.marcenariapro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public UsuarioResponseDTO cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dto) {

        return service.salvar(dto);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listar();
    }
}