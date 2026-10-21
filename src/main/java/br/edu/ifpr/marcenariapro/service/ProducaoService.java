package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.ItemOrcamento;
import br.edu.ifpr.marcenariapro.entity.ItemProducao;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.entity.Producao;
import br.edu.ifpr.marcenariapro.enums.StatusPedido;
import br.edu.ifpr.marcenariapro.enums.StatusProducao;
import br.edu.ifpr.marcenariapro.enums.TipoMovimentacao;
import br.edu.ifpr.marcenariapro.repository.ItemOrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.ProducaoRepository;

@Service
public class ProducaoService {

    private final ProducaoRepository producaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final ItemProducaoRepository itemProducaoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final PagamentoRepository pagamentoRepository;

public ProducaoService(
        ProducaoRepository producaoRepository,
        PedidoRepository pedidoRepository,
        ItemProducaoRepository itemProducaoRepository,
        ItemOrcamentoRepository itemOrcamentoRepository,
        MovimentacaoEstoqueService movimentacaoEstoqueService,
        PagamentoRepository pagamentoRepository) {

this.producaoRepository = producaoRepository;
this.pedidoRepository = pedidoRepository;
this.itemProducaoRepository = itemProducaoRepository;
this.itemOrcamentoRepository = itemOrcamentoRepository;
this.movimentacaoEstoqueService = movimentacaoEstoqueService;
this.pagamentoRepository = pagamentoRepository;
}

public ProducaoResponseDTO salvar(ProducaoRequestDTO dto) {

    Pedido pedido = pedidoRepository.findById(dto.pedidoId())
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

  if (pedido.getStatus() != StatusPedido.APROVADO) {
    throw new RuntimeException(
            "Não é possível criar a produção. "
            + "O pedido precisa estar aprovado."
    );
}

    if (producaoRepository.existsByPedidoId(dto.pedidoId())) {
        throw new RuntimeException(
                "Não é possível criar a produção. "
                + "Este pedido já possui uma produção."
        );
    }

    if (dto.status() != StatusProducao.AGUARDANDO) {
        throw new RuntimeException(
                "Uma nova produção deve iniciar com o status AGUARDANDO."
        );
    }

    Producao producao = new Producao();

    producao.setStatus(dto.status());
    producao.setPedido(pedido);

    Producao producaoSalva = producaoRepository.save(producao);

    // Copia os materiais do orçamento para a produção
    var itensOrcamento = itemOrcamentoRepository
            .findByOrcamentoId(pedido.getOrcamento().getId());

    for (ItemOrcamento itemOrcamento : itensOrcamento) {

        ItemProducao itemProducao = new ItemProducao();

        itemProducao.setQuantidade(itemOrcamento.getQuantidade());
        itemProducao.setMaterial(itemOrcamento.getMaterial());
        itemProducao.setProducao(producaoSalva);

        itemProducaoRepository.save(itemProducao);
    }

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

                Pedido pedido = producao.getPedido();

       

                if (dto.status() == StatusProducao.CANCELADO
        && producao.getStatus() == StatusProducao.EM_PRODUCAO) {

    var itens = itemProducaoRepository.findAll()
            .stream()
            .filter(item ->
                    item.getProducao().getId().equals(id))
            .toList();

    for (var item : itens) {

        var material = item.getMaterial();

        MovimentacaoEstoqueRequestDTO movimentacao =
                new MovimentacaoEstoqueRequestDTO(
                        TipoMovimentacao.ENTRADA,
                       item.getQuantidade().intValue(),
                        material.getId()
                );

        movimentacaoEstoqueService.salvar(movimentacao);
    }
}

                if (producao.getStatus() == StatusProducao.ENTREGUE
                        && dto.status() != StatusProducao.ENTREGUE) {

                          throw new RuntimeException(
                "Não é possível alterar uma produção que já foi entregue."
                        );
        }

        if (dto.status() == StatusProducao.CANCELADO) {

    if (producao.getStatus() == StatusProducao.ENTREGUE) {
        throw new RuntimeException(
                "Não é possível cancelar uma produção que já foi entregue."
        );
    }

    if (producao.getStatus() == StatusProducao.CANCELADO
        && dto.status() != StatusProducao.CANCELADO) {

    throw new RuntimeException(
        "Não é possível alterar uma produção que já foi cancelada."
    );
}

    if (producao.getStatus() == StatusProducao.PRONTO) {
        throw new RuntimeException(
                "Não é possível cancelar uma produção que já está pronta."
        );
    }
}


        if (dto.status().name().equals("EM_PRODUCAO")
                && !producao.getStatus().name().equals("EM_PRODUCAO")) {

                            BigDecimal valorPedido = pedido.getOrcamento().getValor();

          BigDecimal valorMinimoEntrada =
            valorPedido.multiply(new BigDecimal("0.30"));

        BigDecimal valorPago = pagamentoRepository
            .findByPedidoId(pedido.getId())
            .stream()
            .filter(pagamento ->
                    pagamento.getStatus() ==
                    br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO)
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

         if (valorPago.compareTo(valorMinimoEntrada) < 0) {
                throw new RuntimeException(
                "Não é possível iniciar a produção. "
                 + "O pedido precisa ter pelo menos 30% do valor pago.");
                         }

           var itens = itemProducaoRepository.findAll()
        .stream()
        .filter(item -> item.getProducao().getId().equals(id))
        .toList();

// Primeiro verifica se existe estoque suficiente para TODOS os materiais
                for (var item : itens) {

                var material = item.getMaterial();

                if (BigDecimal.valueOf(material.getQuantidade())
                        .compareTo(item.getQuantidade()) < 0) {

                        throw new RuntimeException(
                                "Quantidade insuficiente em estoque para o material: "
                                        + material.getNome());
                }
                }

                // Só depois de todas as validações, realiza as saídas
                for (var item : itens) {

                var material = item.getMaterial();

                MovimentacaoEstoqueRequestDTO movimentacao =
                        new MovimentacaoEstoqueRequestDTO(
                                TipoMovimentacao.SAIDA,
                                item.getQuantidade().intValue(),
                                material.getId()
                        );

                movimentacaoEstoqueService.salvar(movimentacao);
                }
        }

if (dto.status() == StatusProducao.ENTREGUE) {

    if (producao.getStatus() != StatusProducao.PRONTO) {
        throw new RuntimeException(
                "Não é possível entregar a produção. "
                + "A produção precisa estar pronta."
        );
    }

    pedido.setStatus(StatusPedido.ENTREGUE);
    pedidoRepository.save(pedido);
}

producao.setStatus(dto.status());
producao.setPedido(pedido);

Producao producaoAtualizada = producaoRepository.save(producao);



        return converterParaResponse(producaoAtualizada);
    }

  public void excluir(Long id) {

    Producao producao = producaoRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Produção não encontrada"));

    if (producao.getStatus() == StatusProducao.EM_PRODUCAO
            || producao.getStatus() == StatusProducao.PRONTO
            || producao.getStatus() == StatusProducao.ENTREGUE
            || producao.getStatus() == StatusProducao.CANCELADO) {

        throw new RuntimeException(
                "Não é possível excluir uma produção que já foi iniciada, concluída ou cancelada."
        );
    }

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