package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.marcenariapro.dto.MovimentacaoEstoqueRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProducaoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.ItemOrcamento;
import br.edu.ifpr.marcenariapro.entity.ItemProducao;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.entity.Producao;
import br.edu.ifpr.marcenariapro.enums.StatusItemProducao;
import br.edu.ifpr.marcenariapro.enums.StatusPagamento;
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
                        new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível criar a produção. "
                    + "O pedido precisa estar aprovado."
            );
        }

        if (producaoRepository.existsByPedidoId(dto.pedidoId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este pedido já possui uma produção."
            );
        }

        if (dto.status() != StatusProducao.AGUARDANDO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
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
            itemProducao.setStatus(
                    StatusItemProducao.AGUARDANDO_MATERIAL
            );

            ItemProducao itemSalvo =
                    itemProducaoRepository.save(itemProducao);

            reservaMaterialService.reservarItemProducao(itemSalvo);

            if (reservaMaterialService.estaTotalmenteReservado(
                    itemSalvo.getId())) {

                itemSalvo.setStatus(StatusItemProducao.LIBERADO);
                itemProducaoRepository.save(itemSalvo);
            }
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
                        new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produção não encontrada"));

        return converterParaResponse(producao);
    }

    public ProducaoResponseDTO atualizar(
            Long id,
            ProducaoRequestDTO dto) {

        Producao producao = producaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produção não encontrada"));

        Pedido pedido = producao.getPedido();

        StatusProducao statusAtual = producao.getStatus();
        StatusProducao novoStatus = dto.status();

        validarTransicao(statusAtual, novoStatus);

        if (statusAtual == StatusProducao.ENTREGUE
                && novoStatus != StatusProducao.ENTREGUE) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível alterar uma produção que já foi entregue."
            );
        }

        if (statusAtual == StatusProducao.CANCELADO
                && novoStatus != StatusProducao.CANCELADO) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível alterar uma produção que já foi cancelada."
            );
        }

        /*
         * CANCELAMENTO
         */
        if (novoStatus == StatusProducao.CANCELADO) {

            if (statusAtual == StatusProducao.PRONTO
                    || statusAtual == StatusProducao.ENTREGUE) {

                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                        "Não é possível cancelar uma produção pronta ou entregue."
                );
            }

            List<ItemProducao> itens = buscarItens(id);

            if (statusAtual == StatusProducao.AGUARDANDO) {

                /*
                 * Ainda não houve consumo.
                 * Libera as reservas e cancela as necessidades abertas.
                 */
                for (ItemProducao item : itens) {
                    reservaMaterialService.liberarReservaProducao(
                            item.getId()
                    );
                }
            }

            if (statusAtual == StatusProducao.EM_PRODUCAO) {

                /*
                 * A produção já consumiu os materiais.
                 * Devolve as quantidades ao estoque.
                 */
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

        /*
         * INÍCIO DA PRODUÇÃO
         */
        if (novoStatus == StatusProducao.EM_PRODUCAO
                && statusAtual == StatusProducao.AGUARDANDO) {

            List<ItemProducao> itens = buscarItens(id);

            /*
             * Primeiro verifica todos os itens.
             * Nenhuma reserva será consumida se houver pendência.
             */
            for (ItemProducao item : itens) {

                if (item.getStatus()
                        != StatusItemProducao.LIBERADO) {

                    BigDecimal faltante =
                            reservaMaterialService.calcularQuantidadeFaltante(
                                    item.getId());

                    String detalhe;

                    if (faltante.compareTo(BigDecimal.ZERO) > 0) {
                        detalhe = "faltam "
                                + faltante.stripTrailingZeros().toPlainString()
                                + " unidade(s)";
                    } else {
                        detalhe = "a reserva ainda não foi liberada";
                    }

                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Não foi possível iniciar a produção. Material "
                                    + item.getMaterial().getNome()
                                    + ": " + detalhe + "."
                    );
                }

                validarQuantidade(item.getQuantidade());
            }

            /*
             * Exige pagamento mínimo de 30%.
             */
            BigDecimal valorPedido = pedido.getOrcamento().getValor();

            BigDecimal valorMinimoEntrada =
                    valorPedido.multiply(new BigDecimal("0.30"));

            BigDecimal valorPago = pagamentoRepository
                    .findByPedidoId(pedido.getId())
                    .stream()
                    .filter(pagamento ->
                            pagamento.getStatus() == StatusPagamento.PAGO)
                    .map(Pagamento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (valorPago.compareTo(valorMinimoEntrada) < 0) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                        "Não é possível iniciar a produção. "
                        + "O pedido precisa ter pelo menos 30% do valor pago."
                );
            }

            /*
             * Todas as validações passaram.
             * Agora consome as reservas e registra as saídas.
             */
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

                item.setStatus(StatusItemProducao.EM_PRODUCAO);
                itemProducaoRepository.save(item);
            }
        }

        /*
         * CONCLUSÃO DA PRODUÇÃO
         */
        if (novoStatus == StatusProducao.PRONTO
                && statusAtual == StatusProducao.EM_PRODUCAO) {

            List<ItemProducao> itens = buscarItens(id);

            for (ItemProducao item : itens) {
                item.setStatus(StatusItemProducao.CONCLUIDO);
                itemProducaoRepository.save(item);
            }
        }

        /*
         * ENTREGA
         */
        if (novoStatus == StatusProducao.ENTREGUE) {

            if (statusAtual != StatusProducao.PRONTO) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
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
                        new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produção não encontrada"));

        if (producao.getStatus() != StatusProducao.AGUARDANDO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Só é possível excluir uma produção que ainda está aguardando."
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

        return itemProducaoRepository.findByProducaoId(producaoId);
    }

    private void validarQuantidade(BigDecimal quantidade) {

        if (quantidade == null
                || quantidade.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade do material precisa ser maior que zero."
            );
        }

        try {
            quantidade.setScale(0, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade do material precisa ser inteira, "
                    + "pois o controle atual do estoque utiliza unidades inteiras."
            );
        }
    }

    private int converterQuantidade(BigDecimal quantidade) {

        validarQuantidade(quantidade);

        return quantidade.intValueExact();
    }

    private ProducaoResponseDTO converterParaResponse(
            Producao producao) {

        return new ProducaoResponseDTO(
                producao.getId(),
                producao.getStatus(),
                producao.getDataCriacao(),
                producao.getPedido().getId()
        );
    }

    private void validarTransicao(
            StatusProducao statusAtual,
            StatusProducao novoStatus) {

        if (novoStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o status da produção."
            );
        }

        if (statusAtual == novoStatus) {
            return;
        }

        boolean transicaoPermitida = switch (statusAtual) {

            case AGUARDANDO ->
                    novoStatus == StatusProducao.EM_PRODUCAO
                    || novoStatus == StatusProducao.CANCELADO;

            case EM_PRODUCAO ->
                    novoStatus == StatusProducao.PRONTO
                    || novoStatus == StatusProducao.CANCELADO;

            case PRONTO ->
                    novoStatus == StatusProducao.ENTREGUE;

            case ENTREGUE, CANCELADO -> false;
        };

        if (!transicaoPermitida) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transição de status inválida. "
                    + "Verifique a sequência permitida para a produção."
            );
        }
    }
}