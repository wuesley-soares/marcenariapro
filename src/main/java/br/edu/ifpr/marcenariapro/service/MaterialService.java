
package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.MaterialRequestDTO;
import br.edu.ifpr.marcenariapro.dto.MaterialResponseDTO;
import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.MovimentacaoEstoqueRepository;
import br.edu.ifpr.marcenariapro.repository.SolicitacaoSubstituicaoRepository;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ItemProducaoRepository itemProducaoRepository;
    private final SolicitacaoSubstituicaoRepository solicitacaoSubstituicaoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

public MaterialService(
        MaterialRepository materialRepository,
        MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
        ItemProducaoRepository itemProducaoRepository,
        SolicitacaoSubstituicaoRepository solicitacaoSubstituicaoRepository,
        MovimentacaoEstoqueService movimentacaoEstoqueService) {

    this.materialRepository = materialRepository;
    this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    this.itemProducaoRepository = itemProducaoRepository;
    this.solicitacaoSubstituicaoRepository = solicitacaoSubstituicaoRepository;
    this.movimentacaoEstoqueService = movimentacaoEstoqueService;
}

   
@Transactional
public MaterialResponseDTO salvar(MaterialRequestDTO dto) {

    if (dto.nome() == null || dto.nome().isBlank()) {
        throw new RuntimeException("O nome do material é obrigatório.");
    }

    if (dto.quantidade() == null
            || dto.quantidade().compareTo(BigDecimal.ZERO) < 0) {
        throw new RuntimeException("A quantidade não pode ser negativa.");
    }

    if (dto.estoqueMinimo() == null
            || dto.estoqueMinimo().compareTo(BigDecimal.ZERO) < 0) {
        throw new RuntimeException("O estoque mínimo não pode ser negativo.");
    }

    if (dto.valorUnitario() == null
            || dto.valorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException("O valor unitário deve ser maior que zero.");
    }

    if (dto.unidadeMedida() == null) {
        throw new RuntimeException("A unidade de medida é obrigatória.");
    }

    // Cria o material inicialmente com estoque zero.
    Material material = new Material();

    material.setNome(dto.nome());
    material.setDescricao(dto.descricao());
    material.setQuantidade(BigDecimal.ZERO);
    material.setEstoqueMinimo(dto.estoqueMinimo());
    material.setValorUnitario(dto.valorUnitario());
    material.setUnidadeMedida(dto.unidadeMedida());

    Material materialSalvo = materialRepository.save(material);

    // Registra a quantidade inicial como entrada no estoque.
    if (dto.quantidade().compareTo(BigDecimal.ZERO) > 0) {

        MovimentacaoEstoqueRequestDTO movimentacao =
                new MovimentacaoEstoqueRequestDTO(
                        TipoMovimentacao.ENTRADA,
                        dto.quantidade(),
                        materialSalvo.getId()
                );

        movimentacaoEstoqueService.salvar(movimentacao);
    }

    return converterParaResponse(
            materialRepository.findById(materialSalvo.getId())
                    .orElseThrow()
    );
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

    public MaterialResponseDTO atualizar(Long id, MaterialRequestDTO dto) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new RuntimeException("O nome do material é obrigatório.");
        }

        if (dto.estoqueMinimo() == null
                || dto.estoqueMinimo().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("O estoque mínimo não pode ser negativo.");
        }

        if (dto.valorUnitario() == null
                || dto.valorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor unitário deve ser maior que zero.");
        }

        if (dto.unidadeMedida() == null) {
            throw new RuntimeException("A unidade de medida é obrigatória.");
        }

        material.setNome(dto.nome());
        material.setDescricao(dto.descricao());
        material.setEstoqueMinimo(dto.estoqueMinimo());
        material.setValorUnitario(dto.valorUnitario());
        material.setUnidadeMedida(dto.unidadeMedida());

        // A quantidade não é alterada aqui.
        // O estoque deve ser atualizado pelas movimentações.

        Material materialAtualizado = materialRepository.save(material);

        return converterParaResponse(materialAtualizado);
    }

    private MaterialResponseDTO converterParaResponse(Material material) {

        return new MaterialResponseDTO(
                material.getId(),
                material.getNome(),
                material.getDescricao(),
                material.getQuantidade(),
                material.getEstoqueMinimo(),
                material.getValorUnitario(),
                material.getUnidadeMedida()
        );
    }

    public List<MaterialResponseDTO> listarEstoqueBaixo() {

        return materialRepository.findAll()
                .stream()
                .filter(material ->
                        material.getEstoqueMinimo() != null
                        && material.getQuantidade().compareTo(
                                material.getEstoqueMinimo()) <= 0)
                .map(this::converterParaResponse)
                .toList();
    }

    public void excluir(Long id) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        if (movimentacaoEstoqueRepository.existsByMaterialId(id)) {
            throw new RuntimeException(
                    "Não é possível excluir um material que possui movimentações de estoque.");
        }

        if (itemProducaoRepository.existsByMaterialId(id)) {
            throw new RuntimeException(
                    "Não é possível excluir um material que está vinculado à produção.");
        }

        if (solicitacaoSubstituicaoRepository.existsByNovoMaterialId(id)) {
            throw new RuntimeException(
                    "Não é possível excluir um material que possui solicitações de substituição.");
        }

        materialRepository.delete(material);
    }
}
