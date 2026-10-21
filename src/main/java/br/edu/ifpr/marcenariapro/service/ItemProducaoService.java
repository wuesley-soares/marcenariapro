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

    public ItemProducaoService(
            ItemProducaoRepository itemProducaoRepository,
            MaterialRepository materialRepository,
            ProducaoRepository producaoRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService) {

        this.itemProducaoRepository = itemProducaoRepository;
        this.materialRepository = materialRepository;
        this.producaoRepository = producaoRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    // ADICIONAR MATERIAL
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

        if (dto.quantidade() == null
        || dto.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "A quantidade deve ser maior que zero");
        }

        /*
         * Só permite adicionar materiais enquanto
         * a produção estiver aguardando ou em produção.
         */
        if (producao.getStatus() != StatusProducao.AGUARDANDO
                && producao.getStatus() != StatusProducao.EM_PRODUCAO) {

            throw new RuntimeException(
                    "Não é possível adicionar materiais nesta produção");
        }

        /*
         * Se a produção já estiver em andamento,
         * o material precisa ser retirado do estoque
         * imediatamente.
         */
        if (producao.getStatus() == StatusProducao.EM_PRODUCAO) {

            MovimentacaoEstoqueRequestDTO movimentacao =
                    new MovimentacaoEstoqueRequestDTO(
                            TipoMovimentacao.SAIDA,
                           dto.quantidade().intValue(),
                            material.getId()
                    );

            movimentacaoEstoqueService.salvar(movimentacao);
        }

        ItemProducao item = new ItemProducao();

        item.setQuantidade(dto.quantidade());
        item.setProducao(producao);
        item.setMaterial(material);

        ItemProducao salvo = itemProducaoRepository.save(item);

        return new ItemProducaoResponseDTO(
                salvo.getId(),
                salvo.getQuantidade(),
                salvo.getMaterial().getId(),
                salvo.getMaterial().getNome()
        );
    }

    // LISTAR MATERIAIS
    public List<ItemProducaoResponseDTO> listarPorProducao(
            Long producaoId) {

        if (!producaoRepository.existsById(producaoId)) {
            throw new RuntimeException(
                    "Produção não encontrada");
        }

        return itemProducaoRepository
                .findByProducaoId(producaoId)
                .stream()
                .map(item ->
                        new ItemProducaoResponseDTO(
                                item.getId(),
                                item.getQuantidade(),
                                item.getMaterial().getId(),
                                item.getMaterial().getNome()
                        ))
                .toList();
    }

    // REMOVER MATERIAL
    public void excluir(
            Long producaoId,
            Long itemId) {

        Producao producao = producaoRepository.findById(producaoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produção não encontrada"));

        ItemProducao item = itemProducaoRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item de produção não encontrado"));

        // Verifica se o item pertence à produção
        if (!item.getProducao().getId().equals(producao.getId())) {
            throw new RuntimeException(
                    "O material não pertence a esta produção");
        }

        /*
         * Só permite remover enquanto a produção
         * estiver aguardando.
         */
        if (producao.getStatus() != StatusProducao.AGUARDANDO) {
            throw new RuntimeException(
                    "Só é possível remover materiais enquanto a produção estiver aguardando");
        }

        itemProducaoRepository.delete(item);
    }
}