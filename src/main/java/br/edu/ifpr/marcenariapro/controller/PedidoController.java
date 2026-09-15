package br.edu.ifpr.marcenariapro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.marcenariapro.dto.PedidoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PedidoResponseDTO;
import br.edu.ifpr.marcenariapro.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> salvar(
            @RequestBody PedidoRequestDTO dto) {

        PedidoResponseDTO pedido = service.salvar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedido);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
public ResponseEntity<PedidoResponseDTO> buscarPorId(
        @PathVariable Long id) {

    return ResponseEntity.ok(service.buscarPorId(id));
}

@PutMapping("/{id}")
public ResponseEntity<PedidoResponseDTO> atualizar(
        @PathVariable Long id,
        @RequestBody PedidoRequestDTO dto) {

    return ResponseEntity.ok(service.atualizar(id, dto));
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> excluir(@PathVariable Long id) {

    service.excluir(id);

    return ResponseEntity.noContent().build();
}
}