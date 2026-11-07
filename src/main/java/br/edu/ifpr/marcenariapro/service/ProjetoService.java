
package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpr.marcenariapro.dto.ProjetoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProjetoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.entity.Projeto;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ClienteRepository clienteRepository;
    private final AmbienteRepository ambienteRepository;
    private final ComprovanteService comprovanteService;

    public ProjetoService(
            ProjetoRepository projetoRepository,
            ClienteRepository clienteRepository,
            AmbienteRepository ambienteRepository,
            ComprovanteService comprovanteService) {

        this.projetoRepository = projetoRepository;
        this.clienteRepository = clienteRepository;
        this.ambienteRepository = ambienteRepository;
        this.comprovanteService = comprovanteService;
    }

    public ProjetoResponseDTO salvar(
            ProjetoRequestDTO dto,
            MultipartFile documento) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new RuntimeException(
                    "O nome do projeto é obrigatório."
            );
        }

        Projeto projeto = new Projeto();

        projeto.setNome(dto.nome());
        projeto.setDescricao(dto.descricao());
        projeto.setCliente(cliente);

        if (documento != null && !documento.isEmpty()) {
            projeto.setCaminhoDocumento(
                    comprovanteService.salvarPdf(documento)
            );
            projeto.setNomeDocumento(
                    obterNomeArquivo(documento.getOriginalFilename())
            );
        }

        Projeto projetoSalvo = projetoRepository.save(projeto);

        return converterParaResponse(projetoSalvo);
    }

    public List<ProjetoResponseDTO> listar() {

        return projetoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ProjetoResponseDTO buscarPorId(Long id) {

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Projeto não encontrado"));

        return converterParaResponse(projeto);
    }

    public ProjetoResponseDTO atualizar(
            Long id,
            ProjetoRequestDTO dto,
            MultipartFile documento) {

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Projeto não encontrado"));

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new RuntimeException(
                    "O nome do projeto é obrigatório."
            );
        }

        projeto.setNome(dto.nome());
        projeto.setDescricao(dto.descricao());

        String caminhoAntigo = projeto.getCaminhoDocumento();

        if (documento != null && !documento.isEmpty()) {
            String novoCaminho = comprovanteService.salvarPdf(documento);

            projeto.setCaminhoDocumento(novoCaminho);
            projeto.setNomeDocumento(
                    obterNomeArquivo(documento.getOriginalFilename())
            );
        }

        Projeto projetoAtualizado = projetoRepository.save(projeto);

        if (documento != null && !documento.isEmpty()
                && caminhoAntigo != null) {
            comprovanteService.excluir(caminhoAntigo);
        }

        return converterParaResponse(projetoAtualizado);
    }

    public BigDecimal calcularTotalProjeto(Long projetoId) {

        projetoRepository.findById(projetoId)
                .orElseThrow(() ->
                        new RuntimeException("Projeto não encontrado"));

        return projetoRepository.somarOrcamentosPorProjeto(projetoId);
    }

    public void excluir(Long id) {

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Projeto não encontrado"));

        long quantidadeAmbientes =
                ambienteRepository.countByProjetoId(id);

        if (quantidadeAmbientes > 0) {
            throw new RuntimeException(
                    "Não é possível excluir um projeto que possui ambientes."
            );
        }

        projetoRepository.delete(projeto);

        if (projeto.getCaminhoDocumento() != null) {
            comprovanteService.excluir(projeto.getCaminhoDocumento());
        }
    }

    private ProjetoResponseDTO converterParaResponse(Projeto projeto) {

        BigDecimal valorTotal =
                projetoRepository.somarOrcamentosPorProjeto(projeto.getId());

        return new ProjetoResponseDTO(
                projeto.getId(),
                projeto.getNome(),
                projeto.getDescricao(),
                projeto.getDataCriacao(),
                projeto.getCliente().getId(),
                projeto.getCliente().getNome(),
                valorTotal,
                projeto.getNomeDocumento()
        );
    }

    private String obterNomeArquivo(String nome) {

        if (nome == null) {
            return "documento.pdf";
        }

        String nomeLimpo = nome.replace("\\", "/");
        return nomeLimpo.substring(nomeLimpo.lastIndexOf("/") + 1);
    }

    public Resource carregarDocumento(Long id) {

    Projeto projeto = projetoRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Projeto não encontrado"));

    if (projeto.getCaminhoDocumento() == null) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Este projeto não possui documento."
        );
    }

    return comprovanteService.carregar(
            projeto.getCaminhoDocumento()
    );
}
}
