package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.service.AssistenciaTecnicaService;

@RestController
@RequestMapping("/assistencias-tecnicas")
public class AssistenciaTecnicaController {

    private final AssistenciaTecnicaService assistenciaService;

    public AssistenciaTecnicaController(
            AssistenciaTecnicaService assistenciaService) {

        this.assistenciaService = assistenciaService;
    }

    @PostMapping
    public AssistenciaTecnicaResponseDTO salvar(
            @RequestBody AssistenciaTecnicaRequestDTO dto) {

        return assistenciaService.salvar(dto);
    }

    @GetMapping
    public List<AssistenciaTecnicaResponseDTO> listar() {

        return assistenciaService.listar();
    }

    @GetMapping("/{id}")
    public AssistenciaTecnicaResponseDTO buscarPorId(
            @PathVariable Long id) {

        return assistenciaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
public AssistenciaTecnicaResponseDTO atualizar(
        @PathVariable Long id,
        @RequestBody AssistenciaTecnicaRequestDTO dto) {

   return assistenciaService.atualizar(id, dto);
}

@PostMapping("/{id}/materiais")
public ItemAssistenciaTecnicaResponseDTO adicionarMaterial(
        @PathVariable Long id,
        @RequestBody ItemAssistenciaTecnicaRequestDTO dto) {

    return assistenciaService.adicionarMaterial(id, dto);
}

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {

        assistenciaService.excluir(id);
    }
}