package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.ClienteRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ClienteResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {

        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setCpfCnpj(dto.cpfCnpj());
        cliente.setEndereco(dto.endereco());

        Cliente clienteSalvo = repository.save(cliente);

        return converterParaResponse(clienteSalvo);
    }

    public List<ClienteResponseDTO> listar() {

        return repository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    private ClienteResponseDTO converterParaResponse(Cliente cliente) {

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpfCnpj(),
                cliente.getEndereco(),
                cliente.getDataCadastro()
        );
    }
}