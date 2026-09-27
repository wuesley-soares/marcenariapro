package br.edu.ifpr.marcenariapro.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.SolicitacaoSubstituicaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.SolicitacaoSubstituicaoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.ItemProducao;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.SolicitacaoSubstituicao;
import br.edu.ifpr.marcenariapro.enums.StatusSubstituicao;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.SolicitacaoSubstituicaoRepository;

@Service
public class SolicitacaoSubstituicaoService {

    private final SolicitacaoSubstituicaoRepository solicitacaoRepository;
    private final ItemProducaoRepository itemProducaoRepository;
    private final MaterialRepository materialRepository;

    public SolicitacaoSubstituicaoService(
            SolicitacaoSubstituicaoRepository solicitacaoRepository,
            ItemProducaoRepository itemProducaoRepository,
            MaterialRepository materialRepository) {

        this.solicitacaoRepository = solicitacaoRepository;
        this.itemProducaoRepository = itemProducaoRepository;
        this.materialRepository = materialRepository;
    }

    public SolicitacaoSubstituicaoResponseDTO solicitar(
            SolicitacaoSubstituicaoRequestDTO dto) {

        ItemProducao item = itemProducaoRepository.findById(dto.itemProducaoId())
                .orElseThrow(() ->
                        new RuntimeException("Item de produção não encontrado"));

        Material novoMaterial = materialRepository.findById(dto.novoMaterialId())
                .orElseThrow(() ->
                        new RuntimeException("Novo material não encontrado"));

        if (item.getMaterial().getId().equals(novoMaterial.getId())) {
            throw new RuntimeException(
                    "O novo material deve ser diferente do material atual");
        }

        SolicitacaoSubstituicao solicitacao =
                new SolicitacaoSubstituicao();

        solicitacao.setItemProducao(item);
        solicitacao.setNovoMaterial(novoMaterial);
        solicitacao.setStatus(StatusSubstituicao.PENDENTE);

        SolicitacaoSubstituicao salva =
                solicitacaoRepository.save(solicitacao);

        return converterParaResponse(salva);
    }

    public SolicitacaoSubstituicaoResponseDTO aprovar(Long id) {

    SolicitacaoSubstituicao solicitacao =
            solicitacaoRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Solicitação de substituição não encontrada"));

    if (solicitacao.getStatus() != StatusSubstituicao.PENDENTE) {
        throw new RuntimeException(
                "A solicitação não está pendente");
    }

    ItemProducao item = solicitacao.getItemProducao();

    item.setMaterial(solicitacao.getNovoMaterial());

    itemProducaoRepository.save(item);

    solicitacao.setStatus(StatusSubstituicao.APROVADA);

    SolicitacaoSubstituicao aprovada =
            solicitacaoRepository.save(solicitacao);

    return converterParaResponse(aprovada);
}

public SolicitacaoSubstituicaoResponseDTO recusar(Long id) {

    SolicitacaoSubstituicao solicitacao =
            solicitacaoRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Solicitação de substituição não encontrada"));

    if (solicitacao.getStatus() != StatusSubstituicao.PENDENTE) {
        throw new RuntimeException(
                "A solicitação não está pendente");
    }

    solicitacao.setStatus(StatusSubstituicao.RECUSADA);

    SolicitacaoSubstituicao recusada =
            solicitacaoRepository.save(solicitacao);

    return converterParaResponse(recusada);
}

    private SolicitacaoSubstituicaoResponseDTO converterParaResponse(
            SolicitacaoSubstituicao solicitacao) {

        return new SolicitacaoSubstituicaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getItemProducao().getId(),
                solicitacao.getItemProducao().getMaterial().getId(),
                solicitacao.getItemProducao().getMaterial().getNome(),
                solicitacao.getNovoMaterial().getId(),
                solicitacao.getNovoMaterial().getNome(),
                solicitacao.getStatus()
        );
    }
}