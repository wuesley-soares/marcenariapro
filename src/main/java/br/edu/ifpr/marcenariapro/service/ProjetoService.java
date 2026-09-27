package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.ProjetoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProjetoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.entity.Projeto;
import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ClienteRepository clienteRepository;
    private final AmbienteRepository ambienteRepository;

public ProjetoService(
        ProjetoRepository projetoRepository,
        ClienteRepository clienteRepository,
        AmbienteRepository ambienteRepository) {

    this.projetoRepository = projetoRepository;
    this.clienteRepository = clienteRepository;
    this.ambienteRepository = ambienteRepository;
}

    public ProjetoResponseDTO salvar(ProjetoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            if (dto.nome() == null || dto.nome().isBlank()) {
                    throw new RuntimeException(
            "O nome do projeto é obrigatório."
    );
}

        Projeto projeto = new Projeto();

        projeto.setNome(dto.nome());
        projeto.setDescricao(dto.descricao());
        projeto.setCliente(cliente);

        Projeto projetoSalvo = projetoRepository.save(projeto);

        return converterParaResponse(projetoSalvo);
    }

    public List<ProjetoResponseDTO> listar() {

        return projetoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ProjetoResponseDTO buscarPorId(Long id) {

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        return converterParaResponse(projeto);
    }
  public ProjetoResponseDTO atualizar(
        Long id,
        ProjetoRequestDTO dto) {

    Projeto projeto = projetoRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Projeto não encontrado"));

    if (dto.nome() == null || dto.nome().isBlank()) {
        throw new RuntimeException(
                "O nome do projeto é obrigatório."
        );
    }

    projeto.setNome(dto.nome());
    projeto.setDescricao(dto.descricao());

    Projeto projetoAtualizado =
            projetoRepository.save(projeto);

    return converterParaResponse(projetoAtualizado);
}
    public java.math.BigDecimal calcularTotalProjeto(Long projetoId) {

    projetoRepository.findById(projetoId)
            .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

    return projetoRepository.somarOrcamentosPorProjeto(projetoId);
}

public void excluir(Long id) {

    Projeto projeto = projetoRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Projeto não encontrado"));

    long quantidadeAmbientes =
            ambienteRepository.countByProjetoId(id);

    if (quantidadeAmbientes > 0) {
        throw new RuntimeException(
                "Não é possível excluir um projeto que possui ambientes."
        );
    }

    projetoRepository.delete(projeto);
}

private ProjetoResponseDTO converterParaResponse(Projeto projeto) {

    BigDecimal valorTotal =
            projetoRepository.somarOrcamentosPorProjeto(projeto.getId());

    return new ProjetoResponseDTO(
            projeto.getId(),
            projeto.getNome(),
            projeto.getDescricao(),
            projeto.getDataCriacao(),
            projeto.getCliente().getId(),
            projeto.getCliente().getNome(),
            valorTotal
    );
}
}