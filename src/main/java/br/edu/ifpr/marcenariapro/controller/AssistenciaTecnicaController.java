package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.ImagemAssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.enums.EtapaImagemAssistencia;
import br.edu.ifpr.marcenariapro.service.AssistenciaTecnicaService;
import br.edu.ifpr.marcenariapro.service.ImagemAssistenciaTecnicaService;


import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/assistencias-tecnicas")
public class AssistenciaTecnicaController {

    private final AssistenciaTecnicaService assistenciaService;
    private final ImagemAssistenciaTecnicaService imagemService;

    public AssistenciaTecnicaController(
            AssistenciaTecnicaService assistenciaService,
            ImagemAssistenciaTecnicaService imagemService) {

        this.assistenciaService = assistenciaService;
        this.imagemService = imagemService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @PostMapping
    public AssistenciaTecnicaResponseDTO salvar(
            @RequestBody AssistenciaTecnicaRequestDTO dto) {

        return assistenciaService.salvar(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping
    public List<AssistenciaTecnicaResponseDTO> listar() {

        return assistenciaService.listar();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}")
    public AssistenciaTecnicaResponseDTO buscarPorId(
            @PathVariable Long id) {

        return assistenciaService.buscarPorId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @PutMapping("/{id}")
    public AssistenciaTecnicaResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody AssistenciaTecnicaRequestDTO dto) {

        return assistenciaService.atualizar(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {

        assistenciaService.excluir(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}/materiais")
    public List<ItemAssistenciaTecnicaResponseDTO> listarMateriais(
            @PathVariable Long id) {

        return assistenciaService.listarMateriais(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @PostMapping("/{id}/materiais")
    public ItemAssistenciaTecnicaResponseDTO adicionarMaterial(
            @PathVariable Long id,
            @RequestBody ItemAssistenciaTecnicaRequestDTO dto) {

        return assistenciaService.adicionarMaterial(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @DeleteMapping("/{assistenciaId}/materiais/{itemId}")
    public void excluirMaterial(
            @PathVariable Long assistenciaId,
            @PathVariable Long itemId) {

        assistenciaService.excluirMaterial(assistenciaId, itemId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @PostMapping(
            value = "/{id}/imagens",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ImagemAssistenciaTecnicaResponseDTO adicionarImagem(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("etapa") EtapaImagemAssistencia etapa) {

        return imagemService.salvar(id, etapa, arquivo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}/imagens")
    public List<ImagemAssistenciaTecnicaResponseDTO> listarImagens(
            @PathVariable Long id,
            @RequestParam(required = false) EtapaImagemAssistencia etapa) {

        return imagemService.listar(id, etapa);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}/imagens/{imagemId}/arquivo")
    public ResponseEntity<Resource> buscarArquivoImagem(
            @PathVariable Long id,
            @PathVariable Long imagemId) {

        Resource arquivo = imagemService.buscarArquivo(id, imagemId);

        return ResponseEntity.ok()
                .contentType(    org.springframework.http.MediaTypeFactory
        .getMediaType(arquivo)
        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(
                                        arquivo.getFilename(),
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
                )
                .body(arquivo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @DeleteMapping("/{id}/imagens/{imagemId}")
    public void excluirImagem(
            @PathVariable Long id,
            @PathVariable Long imagemId) {

        imagemService.excluir(id, imagemId);
    }
}