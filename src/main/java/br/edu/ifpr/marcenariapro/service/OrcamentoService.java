package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.OrcamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.OrcamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Ambiente;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final AmbienteRepository ambienteRepository;

    public OrcamentoService(
            OrcamentoRepository orcamentoRepository,
            AmbienteRepository ambienteRepository) {

        this.orcamentoRepository = orcamentoRepository;
        this.ambienteRepository = ambienteRepository;
    }

    public OrcamentoResponseDTO salvar(OrcamentoRequestDTO dto) {

        Ambiente ambiente = ambienteRepository.findById(dto.ambienteId())
                .orElseThrow(() -> new RuntimeException("Ambiente não encontrado"));

        Orcamento orcamento = new Orcamento();

        orcamento.setValor(dto.valor());
        orcamento.setDescricao(dto.descricao());
        orcamento.setStatus(dto.status());
        orcamento.setAmbiente(ambiente);

        Orcamento orcamentoSalvo = orcamentoRepository.save(orcamento);

        return converterParaResponse(orcamentoSalvo);
    }

    public List<OrcamentoResponseDTO> listar() {

        return orcamentoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public OrcamentoResponseDTO buscarPorId(Long id) {

    Orcamento orcamento = orcamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

           return converterParaResponse(orcamento);
        }

public OrcamentoResponseDTO atualizar(
        Long id,
        OrcamentoRequestDTO dto) {

        Orcamento orcamento = orcamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

         Ambiente ambiente = ambienteRepository.findById(dto.ambienteId())
            .orElseThrow(() -> new RuntimeException("Ambiente não encontrado"));

         orcamento.setValor(dto.valor());
        orcamento.setDescricao(dto.descricao());
        orcamento.setStatus(dto.status());
        orcamento.setAmbiente(ambiente);

         Orcamento orcamentoAtualizado = orcamentoRepository.save(orcamento);

                 return converterParaResponse(orcamentoAtualizado);
}


public void excluir(Long id) {

    Orcamento orcamento = orcamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

    orcamentoRepository.delete(orcamento);
}


    private OrcamentoResponseDTO converterParaResponse(Orcamento orcamento) {

        return new OrcamentoResponseDTO(
                orcamento.getId(),
                orcamento.getValor(),
                orcamento.getDescricao(),
                orcamento.getStatus(),
                orcamento.getDataCriacao(),
                orcamento.getAmbiente().getId(),
                orcamento.getAmbiente().getNome()
        );
    }
}