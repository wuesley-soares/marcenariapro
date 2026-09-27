package br.edu.ifpr.marcenariapro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.OrcamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.OrcamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Ambiente;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final AmbienteRepository ambienteRepository;
    private final PedidoRepository pedidoRepository;

public OrcamentoService(
        OrcamentoRepository orcamentoRepository,
        AmbienteRepository ambienteRepository,
        PedidoRepository pedidoRepository) {

    this.orcamentoRepository = orcamentoRepository;
    this.ambienteRepository = ambienteRepository;
    this.pedidoRepository = pedidoRepository;
}

    public OrcamentoResponseDTO salvar(OrcamentoRequestDTO dto) {

        Ambiente ambiente = ambienteRepository.findById(dto.ambienteId())
                .orElseThrow(() -> new RuntimeException("Ambiente não encontrado"));

        if (orcamentoRepository.findByAmbienteId(dto.ambienteId()).isPresent()) {
            throw new RuntimeException(
            "Este ambiente já possui um orçamento."
    );
}

     if (dto.status() != StatusOrcamento.PENDENTE) {
         throw new RuntimeException(
            "Um novo orçamento deve iniciar com o status PENDENTE."
         );
    }

    if (dto.valor() == null || dto.valor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
    throw new RuntimeException(
            "O valor do orçamento deve ser maior que zero."
    );
}

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

 

            if (!orcamento.getAmbiente().getId().equals(dto.ambienteId())) {
    throw new RuntimeException(
            "Não é possível alterar o ambiente de um orçamento existente."
             );
            }

            if (dto.valor() == null
        || dto.valor().compareTo(java.math.BigDecimal.ZERO) <= 0) {

    throw new RuntimeException(
            "O valor do orçamento deve ser maior que zero."
    );
}

            if (dto.status() == StatusOrcamento.APROVADO
        && orcamento.getDataValidade() != null
        && LocalDateTime.now().isAfter(orcamento.getDataValidade())) {

    throw new RuntimeException(
            "Não é possível aprovar um orçamento vencido."
             );
            }

         if (pedidoRepository.findByOrcamentoId(id).isPresent()
             && orcamento.getStatus() != dto.status()) {

            throw new RuntimeException(
            "Não é possível alterar o status de um orçamento que já possui um pedido."
    );
}

         if (orcamento.getStatus() == StatusOrcamento.APROVADO
             && dto.status() == StatusOrcamento.PENDENTE) {

            throw new RuntimeException(
            "Não é possível voltar um orçamento aprovado para PENDENTE."
         );
        }

        if (orcamento.getStatus() == StatusOrcamento.APROVADO
        && (!orcamento.getValor().equals(dto.valor())
        || !orcamento.getDescricao().equals(dto.descricao())
        || !orcamento.getAmbiente().getId().equals(dto.ambienteId()))) {

    throw new RuntimeException(
            "Não é possível alterar os dados de um orçamento aprovado."
    );
}

if (orcamento.getStatus() == StatusOrcamento.RECUSADO
        && dto.status() == StatusOrcamento.APROVADO) {

    throw new RuntimeException(
            "Não é possível aprovar novamente um orçamento recusado."
    );
}

if (orcamento.getStatus() == StatusOrcamento.RECUSADO
        && dto.status() != StatusOrcamento.RECUSADO) {

    throw new RuntimeException(
            "Não é possível alterar um orçamento que foi recusado."
    );
}


         orcamento.setValor(dto.valor());
        orcamento.setDescricao(dto.descricao());
        orcamento.setStatus(dto.status());
        

         Orcamento orcamentoAtualizado = orcamentoRepository.save(orcamento);

                 return converterParaResponse(orcamentoAtualizado);
}


public void excluir(Long id) {

    Orcamento orcamento = orcamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        if (orcamento.getStatus() == StatusOrcamento.APROVADO) {
            throw new RuntimeException(
            "Não é possível excluir um orçamento aprovado."
    );
}


    if (pedidoRepository.findByOrcamentoId(id).isPresent()) {
    throw new RuntimeException(
            "Não é possível excluir um orçamento que já possui um pedido."
    );
}
    orcamentoRepository.delete(orcamento);
}


private OrcamentoResponseDTO converterParaResponse(Orcamento orcamento) {

    return new OrcamentoResponseDTO(
            orcamento.getId(),
            orcamento.getValor(),
            orcamento.getDescricao(),
            orcamento.getStatus(),
            orcamento.getDataCriacao(),
            orcamento.getDataValidade(),
            orcamento.getAmbiente().getId(),
            orcamento.getAmbiente().getNome()
    );
}
}