package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.marcenariapro.dto.ItemProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.entity.ItemProducao;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.Producao;
import br.edu.ifpr.marcenariapro.enums.StatusItemProducao;
import br.edu.ifpr.marcenariapro.enums.StatusProducao;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.ProducaoRepository;

@Service
public class ItemProducaoService {

    private final ItemProducaoRepository itemProducaoRepository;
    private final MaterialRepository materialRepository;
    private final ProducaoRepository producaoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final ReservaMaterialService reservaMaterialService;

    public ItemProducaoService(
            ItemProducaoRepository itemProducaoRepository,
            MaterialRepository materialRepository,
            ProducaoRepository producaoRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            ReservaMaterialService reservaMaterialService) {

        this.itemProducaoRepository = itemProducaoRepository;
        this.materialRepository = materialRepository;
        this.producaoRepository = producaoRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.reservaMaterialService = reservaMaterialService;
    }

    @Transactional
    public ItemProducaoResponseDTO salvar(
            Long producaoId,
            ItemProducaoRequestDTO dto) {

        Producao producao = producaoRepository.findById(producaoId)
                .orElseThrow(() ->
                        new RuntimeException("Produção não encontrada"));

        Material material = materialRepository.findById(dto.materialId())
                .orElseThrow(() ->
                        new RuntimeException("Material não encontrado"));

        validarQuantidade(dto.quantidade());

        if (producao.getStatus() != StatusProducao.AGUARDANDO
                && producao.getStatus() != StatusProducao.EM_PRODUCAO) {

            throw new RuntimeException(
                    "Não é possível adicionar materiais nesta produção."
            );
        }

        ItemProducao item = new ItemProducao();

        item.setQuantidade(dto.quantidade());
        item.setProducao(producao);
        item.setMaterial(material);

        if (producao.getStatus() == StatusProducao.AGUARDANDO) {

            item.setStatus(StatusItemProducao.AGUARDANDO_MATERIAL);

            ItemProducao salvo = itemProducaoRepository.save(item);

            reservaMaterialService.reservarItemProducao(salvo);

            if (reservaMaterialService.estaTotalmenteReservado(salvo.getId())) {
                salvo.setStatus(StatusItemProducao.LIBERADO);
            }

            return converterParaResponse(
                    itemProducaoRepository.save(salvo)
            );
        }

        /*
         * Se a produção já estiver em andamento,
         * registra a saída do material imediatamente.
         */
        MovimentacaoEstoqueRequestDTO movimentacao =
                new MovimentacaoEstoqueRequestDTO(
                        TipoMovimentacao.SAIDA,
                        dto.quantidade(),
                        material.getId()
                );

        movimentacaoEstoqueService.salvar(movimentacao);

        item.setStatus(StatusItemProducao.EM_PRODUCAO);

        ItemProducao salvo = itemProducaoRepository.save(item);

        return converterParaResponse(salvo);
    }

    public List<ItemProducaoResponseDTO> listarPorProducao(
            Long producaoId) {

        if (!producaoRepository.existsById(producaoId)) {
            throw new RuntimeException("Produção não encontrada");
        }

        return itemProducaoRepository.findByProducaoId(producaoId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional
    public void excluir(Long producaoId, Long itemId) {

        Producao producao = producaoRepository.findById(producaoId)
                .orElseThrow(() ->
                        new RuntimeException("Produção não encontrada"));

        ItemProducao item = itemProducaoRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item de produção não encontrado"));

        if (!item.getProducao().getId().equals(producao.getId())) {
            throw new RuntimeException(
                    "O material não pertence a esta produção."
            );
        }

        if (producao.getStatus() != StatusProducao.AGUARDANDO) {
            throw new RuntimeException(
                    "Só é possível remover materiais enquanto a produção estiver aguardando."
            );
        }

        reservaMaterialService.removerReservaProducao(item.getId());

        itemProducaoRepository.delete(item);
    }

private void validarQuantidade(BigDecimal quantidade) {

    if (quantidade == null
            || quantidade.compareTo(BigDecimal.ZERO) <= 0) {

        throw new RuntimeException(
                "A quantidade deve ser maior que zero."
        );
    }
}

    private ItemProducaoResponseDTO converterParaResponse(
            ItemProducao item) {

        return new ItemProducaoResponseDTO(
                item.getId(),
                item.getQuantidade(),
                item.getMaterial().getId(),
                item.getMaterial().getNome(),
                item.getStatus()
        );
    }
}