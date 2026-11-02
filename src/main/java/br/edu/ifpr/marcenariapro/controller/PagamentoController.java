package br.edu.ifpr.marcenariapro.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpr.marcenariapro.dto.FinanceiroPedidoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PagamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.service.ComprovanteService;
import br.edu.ifpr.marcenariapro.service.PagamentoService;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final ComprovanteService comprovanteService;

    public PagamentoController(
            PagamentoService pagamentoService,
            ComprovanteService comprovanteService) {

        this.pagamentoService = pagamentoService;
        this.comprovanteService = comprovanteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PagamentoResponseDTO salvar(
            @RequestPart("dados") PagamentoRequestDTO dto,
            @RequestPart(value = "comprovante", required = false)
            MultipartFile comprovante) {

        return pagamentoService.salvar(dto, comprovante);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public List<PagamentoResponseDTO> listar() {
        return pagamentoService.listar();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/pedido/{pedidoId}")
    public List<PagamentoResponseDTO> listarPorPedido(
            @PathVariable Long pedidoId) {

        return pagamentoService.listarPorPedido(pedidoId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/pedido/{pedidoId}/resumo")
    public FinanceiroPedidoResponseDTO resumoFinanceiro(
            @PathVariable Long pedidoId) {

        return pagamentoService.resumoFinanceiro(pedidoId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/{pagamentoId}/comprovante")
    public ResponseEntity<Resource> visualizarComprovante(
            @PathVariable Long pagamentoId) {

        Pagamento pagamento =
                pagamentoService.buscarComprovante(pagamentoId);

        Resource arquivo = comprovanteService.carregar(
                pagamento.getCaminhoComprovante()
        );

        MediaType tipo;

        try {
            String contentType = Files.probeContentType(
                    Paths.get(pagamento.getCaminhoComprovante())
            );

            tipo = contentType != null
                    ? MediaType.parseMediaType(contentType)
                    : MediaType.APPLICATION_OCTET_STREAM;

        } catch (Exception e) {
            tipo = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(pagamento.getNomeComprovante())
                                .build()
                                .toString()
                )
                .body(arquivo);
    }
}