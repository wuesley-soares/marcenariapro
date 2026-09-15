package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.ProjetoRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ProjetoResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.entity.Projeto;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ClienteRepository clienteRepository;

    public ProjetoService(
            ProjetoRepository projetoRepository,
            ClienteRepository clienteRepository) {

        this.projetoRepository = projetoRepository;
        this.clienteRepository = clienteRepository;
    }

    public ProjetoResponseDTO salvar(ProjetoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

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

    private ProjetoResponseDTO converterParaResponse(Projeto projeto) {

        return new ProjetoResponseDTO(
                projeto.getId(),
                projeto.getNome(),
                projeto.getDescricao(),
                projeto.getDataCriacao(),
                projeto.getCliente().getId(),
                projeto.getCliente().getNome()
        );
    }
}