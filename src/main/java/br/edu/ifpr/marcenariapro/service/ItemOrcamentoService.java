package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.ItemOrcamentoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemOrcamentoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.ItemOrcamento;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;
import br.edu.ifpr.marcenariapro.repository.ItemOrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;


@Service
public class ItemOrcamentoService {

    private final ItemOrcamentoRepository itemRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final MaterialRepository materialRepository;
   private final OrcamentoService orcamentoService;

    public ItemOrcamentoService(
            ItemOrcamentoRepository itemRepository,
            OrcamentoRepository orcamentoRepository,
            MaterialRepository materialRepository,
             OrcamentoService orcamentoService) {

        this.itemRepository = itemRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.materialRepository = materialRepository;
        this.orcamentoService=orcamentoService;
    }

    public ItemOrcamentoResponseDTO salvar(
            Long orcamentoId,
            ItemOrcamentoRequestDTO dto) {

        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() ->
                        new RuntimeException("Orçamento não encontrado"));

        if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
            throw new RuntimeException(
                    "Só é possível adicionar materiais a um orçamento PENDENTE."
            );
        }

        if (dto.materialId() == null) {
            throw new RuntimeException(
                    "O material é obrigatório."
            );
        }

        if (dto.quantidade() == null
                || dto.quantidade().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "A quantidade deve ser maior que zero."
            );
        }

        if (dto.valorUnitario() == null
                || dto.valorUnitario().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "O valor unitário deve ser maior que zero."
            );
        }

        if (dto.unidade() == null
                || dto.unidade().isBlank()) {

            throw new RuntimeException(
                    "A unidade é obrigatória."
            );
        }

        Material material = materialRepository.findById(dto.materialId())
                .orElseThrow(() ->
                        new RuntimeException("Material não encontrado"));

ItemOrcamento itemExistente =
        itemRepository
                .findByOrcamentoIdAndMaterialId(
                        orcamentoId,
                        dto.materialId())
                .orElse(null);

if (itemExistente != null) {

    BigDecimal novaQuantidade =
            itemExistente.getQuantidade()
                    .add(dto.quantidade());

    itemExistente.setQuantidade(novaQuantidade);

    ItemOrcamento itemSalvo =
            itemRepository.save(itemExistente);

    orcamentoService.recalcularValor(orcamentoId);

    return converterParaResponse(itemSalvo);
}

ItemOrcamento item = new ItemOrcamento();

item.setQuantidade(dto.quantidade());
item.setUnidade(dto.unidade());
item.setValorUnitario(dto.valorUnitario());
item.setMaterial(material);
item.setOrcamento(orcamento);

ItemOrcamento itemSalvo =
        itemRepository.save(item);

orcamentoService.recalcularValor(orcamentoId);

return converterParaResponse(itemSalvo);
    }

    public List<ItemOrcamentoResponseDTO> listarPorOrcamento(
            Long orcamentoId) {

        return itemRepository
                .findByOrcamentoId(orcamentoId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

public void excluir(Long id) {

    ItemOrcamento item = itemRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Item do orçamento não encontrado"));

    if (item.getOrcamento().getStatus()
            != StatusOrcamento.PENDENTE) {

        throw new RuntimeException(
                "Não é possível excluir materiais de um orçamento que não está PENDENTE."
        );
    }

    itemRepository.delete(item);

    orcamentoService.recalcularValor(
            item.getOrcamento().getId()
    );
}

    private ItemOrcamentoResponseDTO converterParaResponse(
            ItemOrcamento item) {

        BigDecimal subtotal =
                item.getQuantidade()
                        .multiply(item.getValorUnitario());

        return new ItemOrcamentoResponseDTO(
                item.getId(),
                item.getQuantidade(),
                item.getUnidade(),
                item.getValorUnitario(),
                subtotal,
                item.getMaterial().getId(),
                item.getMaterial().getNome()
        );
    }
}