package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

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

import br.edu.ifpr.marcenariapro.dto.ClienteDetalheDTO;
import br.edu.ifpr.marcenariapro.dto.ClienteRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ClienteResponseDTO;
import br.edu.ifpr.marcenariapro.dto.FinanceiroClienteDTO;
import br.edu.ifpr.marcenariapro.service.ClienteService;
import br.edu.ifpr.marcenariapro.dto.ClienteDetalheDTO;
import br.edu.ifpr.marcenariapro.dto.FinanceiroClienteDTO;


@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @RequestBody ClienteRequestDTO dto) {

        ClienteResponseDTO cliente = service.salvar(dto);

        return ResponseEntity.ok(cliente);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ClienteRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
@GetMapping("/{id}/detalhes")
public ResponseEntity<ClienteDetalheDTO> buscarDetalhes(
        @PathVariable Long id) {

    return ResponseEntity.ok(service.buscarDetalhes(id));
}

@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'MARCENEIRO')")
@GetMapping("/{id}/financeiro")
public ResponseEntity<FinanceiroClienteDTO> buscarFinanceiro(
        @PathVariable Long id) {

    return ResponseEntity.ok(service.buscarFinanceiro(id));
}
}