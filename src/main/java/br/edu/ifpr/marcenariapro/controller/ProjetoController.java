
package br.edu.ifpr.marcenariapro.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpr.marcenariapro.dto.ProjetoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProjetoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Projeto;
import br.edu.ifpr.marcenariapro.service.ProjetoService;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService service;

    public ProjetoController(ProjetoService service) {
        this.service = service;
    }
   

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjetoResponseDTO> salvar(
            @RequestPart("dados") ProjetoRequestDTO dto,
            @RequestPart(value = "documento", required = false)
            MultipartFile documento) {

        return ResponseEntity.ok(service.salvar(dto, documento));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjetoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestPart("dados") ProjetoRequestDTO dto,
            @RequestPart(value = "documento", required = false)
            MultipartFile documento) {

        return ResponseEntity.ok(service.atualizar(id, dto, documento));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}/documento")
    public ResponseEntity<Resource> visualizarDocumento(
            @PathVariable Long id) {

        ProjetoResponseDTO projeto = service.buscarPorId(id);
        Resource arquivo = service.carregarDocumento(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(projeto.nomeDocumento())
                                .build()
                                .toString()
                )
                .body(arquivo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}/total")
    public BigDecimal calcularTotalProjeto(
            @PathVariable Long id) {

        return service.calcularTotalProjeto(id);
    }
}
