package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.PedidoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.PedidoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final OrcamentoRepository orcamentoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            OrcamentoRepository orcamentoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    public PedidoResponseDTO salvar(PedidoRequestDTO dto) {

        Orcamento orcamento = orcamentoRepository.findById(dto.orcamentoId())
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        Pedido pedido = new Pedido();

        pedido.setStatus(dto.status());
        pedido.setOrcamento(orcamento);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return converterParaResponse(pedidoSalvo);
    }

    public List<PedidoResponseDTO> listar() {

        return pedidoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    return converterParaResponse(pedido);
}

public PedidoResponseDTO atualizar(
        Long id,
        PedidoRequestDTO dto) {

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    Orcamento orcamento = orcamentoRepository.findById(dto.orcamentoId())
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

    pedido.setStatus(dto.status());
    pedido.setOrcamento(orcamento);

    Pedido pedidoAtualizado = pedidoRepository.save(pedido);

    return converterParaResponse(pedidoAtualizado);
}

public void excluir(Long id) {

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    pedidoRepository.delete(pedido);
}

    private PedidoResponseDTO converterParaResponse(Pedido pedido) {

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getOrcamento().getId()
        );
    }
}