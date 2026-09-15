package br.edu.ifpr.marcenariapro.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.ItemProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.ItemProducao;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.Producao;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.ProducaoRepository;

@Service
public class ItemProducaoService {

    private final ItemProducaoRepository itemProducaoRepository;
    private final MaterialRepository materialRepository;
    private final ProducaoRepository producaoRepository;

    public ItemProducaoService(
            ItemProducaoRepository itemProducaoRepository,
            MaterialRepository materialRepository,
            ProducaoRepository producaoRepository) {

        this.itemProducaoRepository = itemProducaoRepository;
        this.materialRepository = materialRepository;
        this.producaoRepository = producaoRepository;
    }

    public ItemProducaoResponseDTO salvar(
            Long producaoId,
            ItemProducaoRequestDTO dto) {

        Producao producao = producaoRepository.findById(producaoId)
                .orElseThrow(() ->
                        new RuntimeException("Produção não encontrada"));

        Material material = materialRepository.findById(dto.materialId())
                .orElseThrow(() ->
                        new RuntimeException("Material não encontrado"));

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
}