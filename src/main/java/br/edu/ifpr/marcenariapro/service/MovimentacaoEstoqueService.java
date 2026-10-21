package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.MovimentacaoEstoque;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.MovimentacaoEstoqueRepository;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final MaterialRepository materialRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoRepository,
            MaterialRepository materialRepository) {

        this.movimentacaoRepository = movimentacaoRepository;
        this.materialRepository = materialRepository;
    }

    public MovimentacaoEstoqueResponseDTO salvar(
            MovimentacaoEstoqueRequestDTO dto) {

        Material material = materialRepository.findById(dto.materialId())
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        if (dto.quantidade() <= 0) {
            throw new RuntimeException("A quantidade deve ser maior que zero");
        }

        if (dto.tipo() == TipoMovimentacao.SAIDA
                && material.getQuantidade() < dto.quantidade()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantidade insuficiente em estoque"
            );
        }

        if (dto.tipo() == TipoMovimentacao.ENTRADA) {
            material.setQuantidade(
                    material.getQuantidade() + dto.quantidade());
        }

        if (dto.tipo() == TipoMovimentacao.SAIDA) {
            material.setQuantidade(
                    material.getQuantidade() - dto.quantidade());
        }

        materialRepository.save(material);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setTipo(dto.tipo());
        movimentacao.setQuantidade(dto.quantidade());
        movimentacao.setMaterial(material);

        MovimentacaoEstoque movimentacaoSalva =
                movimentacaoRepository.save(movimentacao);

        return converterParaResponse(movimentacaoSalva);
    }

    public List<MovimentacaoEstoqueResponseDTO> listar() {

          return movimentacaoRepository.findAll()
            .stream()
            .sorted((a, b) ->
                    b.getDataMovimentacao()
                            .compareTo(a.getDataMovimentacao()))
            .map(this::converterParaResponse)
            .toList();
    }

    public MovimentacaoEstoqueResponseDTO buscarPorId(Long id) {

        MovimentacaoEstoque movimentacao =
                movimentacaoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Movimentação não encontrada"));

        return converterParaResponse(movimentacao);
    }

  public void excluir(Long id) {

    movimentacaoRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Movimentação não encontrada"));

    throw new RuntimeException(
            "Não é possível excluir uma movimentação de estoque.");
}

    private MovimentacaoEstoqueResponseDTO converterParaResponse(
            MovimentacaoEstoque movimentacao) {

        return new MovimentacaoEstoqueResponseDTO(
                movimentacao.getId(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataMovimentacao(),
                movimentacao.getMaterial().getId(),
                movimentacao.getMaterial().getNome()
        );
    }
}