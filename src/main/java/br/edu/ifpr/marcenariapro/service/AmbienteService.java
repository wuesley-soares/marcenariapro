package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.AmbienteRequestDTO;
import br.edu.ifpr.marcenariapro.dto.AmbienteResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Ambiente;
import br.edu.ifpr.marcenariapro.entity.Projeto;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;

@Service
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;
    private final ProjetoRepository projetoRepository;
    private final OrcamentoRepository orcamentoRepository;

    public AmbienteService(
        AmbienteRepository ambienteRepository,
        ProjetoRepository projetoRepository,
        OrcamentoRepository orcamentoRepository) {

    this.ambienteRepository = ambienteRepository;
    this.projetoRepository = projetoRepository;
    this.orcamentoRepository = orcamentoRepository;
}

    public AmbienteResponseDTO salvar(AmbienteRequestDTO dto) {

        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

                if (dto.nome() == null || dto.nome().isBlank()) {
                 throw new RuntimeException(
            "O nome do ambiente é obrigatório."
    );
}

        Ambiente ambiente = new Ambiente();

        ambiente.setNome(dto.nome());
        ambiente.setDescricao(dto.descricao());
        ambiente.setProjeto(projeto);

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
                .orElseThrow(() -> new RuntimeException("Ambiente não encontrado"));

        return converterParaResponse(ambiente);
    }

    public AmbienteResponseDTO atualizar(
        Long id,
        AmbienteRequestDTO dto) {

    Ambiente ambiente = ambienteRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Ambiente não encontrado"));

    Projeto projeto = projetoRepository.findById(dto.projetoId())
            .orElseThrow(() ->
                    new RuntimeException("Projeto não encontrado"));

    if (dto.nome() == null || dto.nome().isBlank()) {
        throw new RuntimeException(
                "O nome do ambiente é obrigatório."
        );
    }

    ambiente.setNome(dto.nome());
    ambiente.setDescricao(dto.descricao());
    ambiente.setProjeto(projeto);

    Ambiente ambienteAtualizado =
            ambienteRepository.save(ambiente);

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
}
    private AmbienteResponseDTO converterParaResponse(Ambiente ambiente) {

        return new AmbienteResponseDTO(
                ambiente.getId(),
                ambiente.getNome(),
                ambiente.getDescricao(),
                ambiente.getDataCriacao(),
                ambiente.getProjeto().getId(),
                ambiente.getProjeto().getNome()
        );
    }
}