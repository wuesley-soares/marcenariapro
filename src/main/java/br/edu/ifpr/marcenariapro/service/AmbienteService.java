
package br.edu.ifpr.marcenariapro.service;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import br.edu.ifpr.marcenariapro.dto.AmbienteRequestDTO;
import br.edu.ifpr.marcenariapro.dto.AmbienteResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Ambiente;
import br.edu.ifpr.marcenariapro.entity.Projeto;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;
    private final ProjetoRepository projetoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ComprovanteService comprovanteService;

    public AmbienteService(
            AmbienteRepository ambienteRepository,
            ProjetoRepository projetoRepository,
            OrcamentoRepository orcamentoRepository,
            ComprovanteService comprovanteService) {

        this.ambienteRepository = ambienteRepository;
        this.projetoRepository = projetoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.comprovanteService = comprovanteService;
    }

    public AmbienteResponseDTO salvar(
            AmbienteRequestDTO dto,
            MultipartFile documento) {

        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() ->
                        new RuntimeException("Projeto não encontrado"));

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new RuntimeException(
                    "O nome do ambiente é obrigatório."
            );
        }

        Ambiente ambiente = new Ambiente();

        ambiente.setNome(dto.nome());
        ambiente.setDescricao(dto.descricao());
        ambiente.setProjeto(projeto);

        if (documento != null && !documento.isEmpty()) {
            ambiente.setCaminhoDocumento(
                    comprovanteService.salvarPdf(documento)
            );
            ambiente.setNomeDocumento(
                    obterNomeArquivo(documento.getOriginalFilename())
            );
        }

        Ambiente ambienteSalvo = ambienteRepository.save(ambiente);

        return converterParaResponse(ambienteSalvo);
    }

    public List<AmbienteResponseDTO> listar() {

        return ambienteRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public AmbienteResponseDTO buscarPorId(Long id) {

        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ambiente não encontrado"));

        return converterParaResponse(ambiente);
    }

    public AmbienteResponseDTO atualizar(
            Long id,
            AmbienteRequestDTO dto,
            MultipartFile documento) {

        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ambiente não encontrado"));

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new RuntimeException(
                    "O nome do ambiente é obrigatório."
            );
        }

        ambiente.setNome(dto.nome());
        ambiente.setDescricao(dto.descricao());

        String caminhoAntigo = ambiente.getCaminhoDocumento();

        if (documento != null && !documento.isEmpty()) {
            String novoCaminho = comprovanteService.salvarPdf(documento);

            ambiente.setCaminhoDocumento(novoCaminho);
            ambiente.setNomeDocumento(
                    obterNomeArquivo(documento.getOriginalFilename())
            );
        }

        Ambiente ambienteAtualizado =
                ambienteRepository.save(ambiente);

        if (documento != null && !documento.isEmpty()
                && caminhoAntigo != null) {
            comprovanteService.excluir(caminhoAntigo);
        }

        return converterParaResponse(ambienteAtualizado);
    }

    public void excluir(Long id) {

        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ambiente não encontrado"));

        long quantidadeOrcamentos =
                orcamentoRepository.countByAmbienteId(id);

        if (quantidadeOrcamentos > 0) {
            throw new RuntimeException(
                    "Não é possível excluir um ambiente que possui orçamento."
            );
        }

        ambienteRepository.delete(ambiente);

        if (ambiente.getCaminhoDocumento() != null) {
            comprovanteService.excluir(ambiente.getCaminhoDocumento());
        }
    }

    private AmbienteResponseDTO converterParaResponse(Ambiente ambiente) {

        return new AmbienteResponseDTO(
                ambiente.getId(),
                ambiente.getNome(),
                ambiente.getDescricao(),
                ambiente.getDataCriacao(),
                ambiente.getProjeto().getId(),
                ambiente.getProjeto().getNome(),
                ambiente.getNomeDocumento()
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

    Ambiente ambiente = ambienteRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Ambiente não encontrado"));

    if (ambiente.getCaminhoDocumento() == null) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Este ambiente não possui documento."
        );
    }

    return comprovanteService.carregar(
            ambiente.getCaminhoDocumento()
    );
}
}
