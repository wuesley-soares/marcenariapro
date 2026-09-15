package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.entity.Producao;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.ProducaoRepository;

@Service
public class ProducaoService {

    private final ProducaoRepository producaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemProducaoRepository itemProducaoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public ProducaoService(
            ProducaoRepository producaoRepository,
            PedidoRepository pedidoRepository,
            ItemProducaoRepository itemProducaoRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService) {

        this.producaoRepository = producaoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemProducaoRepository = itemProducaoRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    public ProducaoResponseDTO salvar(ProducaoRequestDTO dto) {

        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Producao producao = new Producao();

        producao.setStatus(dto.status());
        producao.setPedido(pedido);

        Producao producaoSalva = producaoRepository.save(producao);

        return converterParaResponse(producaoSalva);
    }

    public List<ProducaoResponseDTO> listar() {

        return producaoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ProducaoResponseDTO buscarPorId(Long id) {

        Producao producao = producaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produção não encontrada"));

        return converterParaResponse(producao);
    }

    public ProducaoResponseDTO atualizar(
            Long id,
            ProducaoRequestDTO dto) {

        Producao producao = producaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produção não encontrada"));

        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (dto.status().name().equals("EM_PRODUCAO")
                && !producao.getStatus().name().equals("EM_PRODUCAO")) {

            var itens = itemProducaoRepository.findAll()
                    .stream()
                    .filter(item -> item.getProducao().getId().equals(id))
                    .toList();

            for (var item : itens) {

                var material = item.getMaterial();

                if (material.getQuantidade() < item.getQuantidade()) {
                    throw new RuntimeException(
                            "Quantidade insuficiente em estoque para o material: "
                                    + material.getNome());
                }

                MovimentacaoEstoqueRequestDTO movimentacao =
                        new MovimentacaoEstoqueRequestDTO(
                                TipoMovimentacao.SAIDA,
                                item.getQuantidade(),
                                material.getId()
                        );

                movimentacaoEstoqueService.salvar(movimentacao);
            }
        }

        producao.setStatus(dto.status());
        producao.setPedido(pedido);

        Producao producaoAtualizada = producaoRepository.save(producao);

        return converterParaResponse(producaoAtualizada);
    }

    public void excluir(Long id) {

        Producao producao = producaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produção não encontrada"));

        producaoRepository.delete(producao);
    }

    private ProducaoResponseDTO converterParaResponse(Producao producao) {

        return new ProducaoResponseDTO(
                producao.getId(),
                producao.getStatus(),
                producao.getDataCriacao(),
                producao.getPedido().getId()
        );
    }
}