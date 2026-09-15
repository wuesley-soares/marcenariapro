package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.MaterialRequestDTO;
import br.edu.ifpr.marcenariapro.dto.MaterialResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public MaterialResponseDTO salvar(MaterialRequestDTO dto) {

        Material material = new Material();

        material.setNome(dto.nome());
        material.setDescricao(dto.descricao());
        material.setQuantidade(dto.quantidade());
        material.setEstoqueMinimo(dto.estoqueMinimo());
        material.setValorUnitario(dto.valorUnitario());

        Material materialSalvo = materialRepository.save(material);

        return converterParaResponse(materialSalvo);
    }

    public List<MaterialResponseDTO> listar() {

        return materialRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public MaterialResponseDTO buscarPorId(Long id) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        return converterParaResponse(material);
    }

    public MaterialResponseDTO atualizar(
            Long id,
            MaterialRequestDTO dto) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        material.setNome(dto.nome());
        material.setDescricao(dto.descricao());
        material.setQuantidade(dto.quantidade());
        material.setEstoqueMinimo(dto.estoqueMinimo());
        material.setValorUnitario(dto.valorUnitario());

        Material materialAtualizado = materialRepository.save(material);

        return converterParaResponse(materialAtualizado);
    }

    public void excluir(Long id) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        materialRepository.delete(material);
    }

    private MaterialResponseDTO converterParaResponse(Material material) {

        return new MaterialResponseDTO(
                material.getId(),
                material.getNome(),
                material.getDescricao(),
                material.getQuantidade(),
                material.getEstoqueMinimo(),
                material.getValorUnitario()
        );
    }

    public List<MaterialResponseDTO> listarEstoqueBaixo() {

    return materialRepository.findAll()
            .stream()
            .filter(material ->
                    material.getEstoqueMinimo() != null
                    && material.getQuantidade() <= material.getEstoqueMinimo())
            .map(this::converterParaResponse)
            .toList();
}
}