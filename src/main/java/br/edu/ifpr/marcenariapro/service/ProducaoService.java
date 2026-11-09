
package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class ProducaoService {

    private final ProducaoRepository producaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final ItemProducaoRepository itemProducaoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final PagamentoRepository pagamentoRepository;
    private final ReservaMaterialService reservaMaterialService;

    public ProducaoService(
            ProducaoRepository producaoRepository,
            PedidoRepository pedidoRepository,
            ItemProducaoRepository itemProducaoRepository,
            ItemOrcamentoRepository itemOrcamentoRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            PagamentoRepository pagamentoRepository,
            ReservaMaterialService reservaMaterialService) {

        this.producaoRepository = producaoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemProducaoRepository = itemProducaoRepository;
        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.pagamentoRepository = pagamentoRepository;
        this.reservaMaterialService = reservaMaterialService;
    }

    public ProducaoResponseDTO salvar(ProducaoRequestDTO dto) {

        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new RuntimeException(
                    "Não é possível criar a produção. "
                    + "O pedido precisa estar aprovado."
            );
        }

        if (producaoRepository.existsByPedidoId(dto.pedidoId())) {
            throw new RuntimeException(
                    "Este pedido já possui uma produção."
            );
        }

        if (dto.status() != StatusProducao.AGUARDANDO) {
            throw new RuntimeException(
                    "Uma nova produção deve iniciar com o status AGUARDANDO."
            );
        }

        Producao producao = new Producao();
        producao.setStatus(StatusProducao.AGUARDANDO);
        producao.setPedido(pedido);

        Producao producaoSalva = producaoRepository.save(producao);

        List<ItemOrcamento> itensOrcamento =
                itemOrcamentoRepository.findByOrcamentoId(
                        pedido.getOrcamento().getId()
                );

        for (ItemOrcamento itemOrcamento : itensOrcamento) {

            validarQuantidade(itemOrcamento.getQuantidade());

            ItemProducao itemProducao = new ItemProducao();

            itemProducao.setQuantidade(itemOrcamento.getQuantidade());
            itemProducao.setMaterial(itemOrcamento.getMaterial());
            itemProducao.setProducao(producaoSalva);

            ItemProducao itemSalvo =
                    itemProducaoRepository.save(itemProducao);

            // Cria a reserva e, se necessário, a necessidade de compra.
            reservaMaterialService.reservarItemProducao(itemSalvo);
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
                .orElseThrow(() ->
                        new RuntimeException("Produção não encontrada"));

        return converterParaResponse(producao);
    }

    public ProducaoResponseDTO atualizar(
            Long id,
            ProducaoRequestDTO dto) {

        Producao producao = producaoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Produção não encontrada"));

        Pedido pedido = producao.getPedido();

        StatusProducao statusAtual = producao.getStatus();
        StatusProducao novoStatus = dto.status();

        if (statusAtual == StatusProducao.ENTREGUE
                && novoStatus != StatusProducao.ENTREGUE) {

            throw new RuntimeException(
                    "Não é possível alterar uma produção que já foi entregue."
            );
        }

        if (statusAtual == StatusProducao.CANCELADO
                && novoStatus != StatusProducao.CANCELADO) {

            throw new RuntimeException(
                    "Não é possível alterar uma produção que já foi cancelada."
            );
        }

        if (novoStatus == StatusProducao.CANCELADO) {

            if (statusAtual == StatusProducao.ENTREGUE) {
                throw new RuntimeException(
                        "Não é possível cancelar uma produção que já foi entregue."
                );
            }

            if (statusAtual == StatusProducao.PRONTO) {
                throw new RuntimeException(
                        "Não é possível cancelar uma produção que já está pronta."
                );
            }

            if (statusAtual == StatusProducao.AGUARDANDO) {

                // Ainda não houve consumo: apenas libera as reservas.
                List<ItemProducao> itens = buscarItens(id);

                for (ItemProducao item : itens) {
                    reservaMaterialService.liberarReservaProducao(
                            item.getId()
                    );
                }
            }

            if (statusAtual == StatusProducao.EM_PRODUCAO) {

                // A produção já consumiu os materiais.
                // Devolve cada quantidade ao estoque por movimentação.
                List<ItemProducao> itens = buscarItens(id);

                for (ItemProducao item : itens) {

                    int quantidade = converterQuantidade(
                            item.getQuantidade()
                    );

                    MovimentacaoEstoqueRequestDTO movimentacao =
                            new MovimentacaoEstoqueRequestDTO(
                                    TipoMovimentacao.ENTRADA,
                                    quantidade,
                                    item.getMaterial().getId()
                            );

                    movimentacaoEstoqueService.salvar(movimentacao);
                }
            }
        }

        if (novoStatus == StatusProducao.EM_PRODUCAO
                && statusAtual != StatusProducao.EM_PRODUCAO) {

            BigDecimal valorPedido = pedido.getOrcamento().getValor();

            BigDecimal valorMinimoEntrada =
                    valorPedido.multiply(new BigDecimal("0.30"));

            BigDecimal valorPago = pagamentoRepository
                    .findByPedidoId(pedido.getId())
                    .stream()
                    .filter(pagamento ->
                            pagamento.getStatus()
                                    == br.edu.ifpr.marcenariapro.enums.StatusPagamento.PAGO)
                    .map(Pagamento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (valorPago.compareTo(valorMinimoEntrada) < 0) {
                throw new RuntimeException(
                        "Não é possível iniciar a produção. "
                        + "O pedido precisa ter pelo menos 30% do valor pago."
                );
            }

            List<ItemProducao> itens = buscarItens(id);

            // Valida todas as quantidades antes de consumir as reservas.
            for (ItemProducao item : itens) {
                validarQuantidade(item.getQuantidade());
            }

            // Consome as reservas e registra as saídas do estoque.
            for (ItemProducao item : itens) {

                reservaMaterialService.consumirReservaProducao(
                        item.getId()
                );

                MovimentacaoEstoqueRequestDTO movimentacao =
                        new MovimentacaoEstoqueRequestDTO(
                                TipoMovimentacao.SAIDA,
                                converterQuantidade(item.getQuantidade()),
                                item.getMaterial().getId()
                        );

                movimentacaoEstoqueService.salvar(movimentacao);
            }
        }

        if (novoStatus == StatusProducao.ENTREGUE) {

            if (statusAtual != StatusProducao.PRONTO) {
                throw new RuntimeException(
                        "Não é possível entregar a produção. "
                        + "A produção precisa estar pronta."
                );
            }

            pedido.setStatus(StatusPedido.ENTREGUE);
            pedidoRepository.save(pedido);
        }

        producao.setStatus(novoStatus);
        producao.setPedido(pedido);

        Producao producaoAtualizada =
                producaoRepository.save(producao);

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

        List<ItemProducao> itens = buscarItens(id);

        for (ItemProducao item : itens) {
                reservaMaterialService.removerReservaProducao(
                 item.getId()
                );
        }

        producaoRepository.delete(producao);
    }

    private List<ItemProducao> buscarItens(Long producaoId) {

        return itemProducaoRepository.findAll()
                .stream()
                .filter(item ->
                        item.getProducao().getId().equals(producaoId))
                .toList();
    }

    private void validarQuantidade(BigDecimal quantidade) {

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "A quantidade do material precisa ser maior que zero."
            );
        }

        try {
            quantidade.setScale(0, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new RuntimeException(
                    "A quantidade do material precisa ser inteira, "
                    + "pois o controle atual do estoque utiliza unidades inteiras."
            );
        }
    }

    private int converterQuantidade(BigDecimal quantidade) {

        validarQuantidade(quantidade);

        return quantidade.intValueExact();
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
