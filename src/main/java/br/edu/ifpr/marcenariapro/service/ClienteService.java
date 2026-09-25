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

        String cpfCnpj = dto.cpfCnpj() != null
        ? dto.cpfCnpj().trim()
        : null;

        if (dto.nome() == null || dto.nome().isBlank()) {
    throw new RuntimeException(
            "O nome do cliente é obrigatório."
    );
}

if (dto.email() == null || dto.email().isBlank()) {
    throw new RuntimeException(
            "O e-mail do cliente é obrigatório."
    );
}

if (!dto.email().contains("@")) {
    throw new RuntimeException(
            "O e-mail informado é inválido."
    );
}

if (dto.telefone() == null || dto.telefone().isBlank()) {
    throw new RuntimeException(
            "O telefone do cliente é obrigatório."
    );
}

if (repository.findByEmail(dto.email()).isPresent()) {
    throw new RuntimeException(
            "Já existe um cliente cadastrado com este e-mail."
    );
}


        if (dto.cpfCnpj() == null || dto.cpfCnpj().isBlank()) {
    throw new RuntimeException(
            "O CPF/CNPJ é obrigatório."
    );
}

        if (repository.findByCpfCnpj(dto.cpfCnpj()).isPresent()) {
    throw new RuntimeException(
            "Já existe um cliente cadastrado com este CPF/CNPJ."
    );
}

        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setCpfCnpj(cpfCnpj);
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