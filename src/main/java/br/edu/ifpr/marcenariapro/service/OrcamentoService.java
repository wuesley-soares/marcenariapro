package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.OrcamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.OrcamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Ambiente;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.ItemOrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;


@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final AmbienteRepository ambienteRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemOrcamentoRepository itemOrcamentoRepository;

public OrcamentoService(
        OrcamentoRepository orcamentoRepository,
        AmbienteRepository ambienteRepository,
        PedidoRepository pedidoRepository,
        ItemOrcamentoRepository itemOrcamentoRepository) {

    this.orcamentoRepository = orcamentoRepository;
    this.ambienteRepository = ambienteRepository;
    this.pedidoRepository = pedidoRepository;
    this.itemOrcamentoRepository=itemOrcamentoRepository;
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

    if (dto.valorMaoObra() == null ||
        dto.valorMaoObra().compareTo(BigDecimal.ZERO) < 0) {

    throw new RuntimeException(
            "O valor da mão de obra não pode ser negativo."
    );
}

if (dto.margemLucro() == null ||
        dto.margemLucro().compareTo(BigDecimal.ZERO) < 0) {

    throw new RuntimeException(
            "A margem de lucro não pode ser negativa."
    );
}
        Orcamento orcamento = new Orcamento();

        orcamento.setValor(BigDecimal.ZERO);
        orcamento.setValorMaoObra(dto.valorMaoObra());
        orcamento.setMargemLucro(dto.margemLucro());
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
        && (!orcamento.getValorMaoObra().equals(dto.valorMaoObra())
        || !orcamento.getMargemLucro().equals(dto.margemLucro())
        || !orcamento.getDescricao().equals(dto.descricao())
        || !orcamento.getAmbiente().getId().equals(dto.ambienteId()))) {

    throw new RuntimeException(
            "Não é possível alterar os dados de um orçamento aprovado."
    );
}

if (dto.valorMaoObra() == null ||
        dto.valorMaoObra().compareTo(BigDecimal.ZERO) < 0) {

    throw new RuntimeException(
            "O valor da mão de obra não pode ser negativo."
    );
}

if (dto.margemLucro() == null ||
        dto.margemLucro().compareTo(BigDecimal.ZERO) < 0) {

    throw new RuntimeException(
            "A margem de lucro não pode ser negativa."
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


 orcamento.setValorMaoObra(dto.valorMaoObra());
orcamento.setMargemLucro(dto.margemLucro());
orcamento.setDescricao(dto.descricao());
orcamento.setStatus(dto.status());

Orcamento orcamentoAtualizado = orcamentoRepository.save(orcamento);

recalcularValor(orcamentoAtualizado.getId());

orcamentoAtualizado = orcamentoRepository.findById(orcamentoAtualizado.getId())
        .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

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


public void recalcularValor(Long orcamentoId) {

    Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
            .orElseThrow(() ->
                    new RuntimeException("Orçamento não encontrado"));

    BigDecimal totalMateriais =
            itemOrcamentoRepository
                    .findByOrcamentoId(orcamentoId)
                    .stream()
                    .map(item ->
                            item.getQuantidade()
                                    .multiply(item.getValorUnitario()))
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );

    BigDecimal maoDeObra =
            orcamento.getValorMaoObra() != null
                    ? orcamento.getValorMaoObra()
                    : BigDecimal.ZERO;

    BigDecimal margem =
            orcamento.getMargemLucro() != null
                    ? orcamento.getMargemLucro()
                    : BigDecimal.ZERO;

    BigDecimal custoTotal =
            totalMateriais.add(maoDeObra);

    BigDecimal lucro =
            custoTotal
                    .multiply(margem)
                    .divide(
                            BigDecimal.valueOf(100)
                    );

    BigDecimal valorFinal =
            custoTotal.add(lucro);

    orcamento.setValor(valorFinal);

    orcamentoRepository.save(orcamento);
}

private OrcamentoResponseDTO converterParaResponse(Orcamento orcamento) {

    return new OrcamentoResponseDTO(
            orcamento.getId(),
            orcamento.getValor(),
            orcamento.getValorMaoObra(),
            orcamento.getMargemLucro(),
            orcamento.getDescricao(),
            orcamento.getStatus(),
            orcamento.getDataCriacao(),
            orcamento.getDataValidade(),
            orcamento.getAmbiente().getId(),
            orcamento.getAmbiente().getNome(),
            orcamento.getAmbiente().getProjeto().getNome()
    );
}
}