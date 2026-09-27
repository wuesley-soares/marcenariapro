package br.edu.ifpr.marcenariapro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.SolicitacaoSubstituicaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.SolicitacaoSubstituicaoResponseDTO;
import br.edu.ifpr.marcenariapro.service.SolicitacaoSubstituicaoService;

@RestController
@RequestMapping("/solicitacoes-substituicao")
public class SolicitacaoSubstituicaoController {

    private final SolicitacaoSubstituicaoService service;

    public SolicitacaoSubstituicaoController(
            SolicitacaoSubstituicaoService service) {
        this.service = service;
    }

    @PutMapping("/{id}/aprovar")
public ResponseEntity<SolicitacaoSubstituicaoResponseDTO> aprovar(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            service.aprovar(id)
    );
}

@PutMapping("/{id}/recusar")
public ResponseEntity<SolicitacaoSubstituicaoResponseDTO> recusar(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            service.recusar(id)
    );
}

    @PostMapping
    public ResponseEntity<SolicitacaoSubstituicaoResponseDTO> solicitar(
            @RequestBody SolicitacaoSubstituicaoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.solicitar(dto));
    }
}