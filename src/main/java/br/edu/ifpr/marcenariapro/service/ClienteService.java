package br.edu.ifpr.marcenariapro.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.ClienteRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ClienteResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.ProjetoRepository;
import br.edu.ifpr.marcenariapro.repository.AssistenciaTecnicaRepository;

@Service
public class ClienteService {

private final ClienteRepository repository;
private final ProjetoRepository projetoRepository;
private final AssistenciaTecnicaRepository assistenciaTecnicaRepository;

    public ClienteService(
        ClienteRepository repository,
        ProjetoRepository projetoRepository,
        AssistenciaTecnicaRepository assistenciaTecnicaRepository) {

    this.repository = repository;
    this.projetoRepository = projetoRepository;
    this.assistenciaTecnicaRepository = assistenciaTecnicaRepository;
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

    public ClienteResponseDTO buscarPorId(Long id) {

    Cliente cliente = repository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Cliente não encontrado"));

    return converterParaResponse(cliente);
}

public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {

    Cliente cliente = repository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Cliente não encontrado"));

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

    if (cpfCnpj == null || cpfCnpj.isBlank()) {
        throw new RuntimeException(
                "O CPF/CNPJ é obrigatório."
        );
    }

    Optional<Cliente> clienteEmail =
            repository.findByEmail(dto.email());

    if (clienteEmail.isPresent()
            && !clienteEmail.get().getId().equals(id)) {

        throw new RuntimeException(
                "Já existe outro cliente cadastrado com este e-mail."
        );
    }

    Optional<Cliente> clienteCpfCnpj =
            repository.findByCpfCnpj(cpfCnpj);

    if (clienteCpfCnpj.isPresent()
            && !clienteCpfCnpj.get().getId().equals(id)) {

        throw new RuntimeException(
                "Já existe outro cliente cadastrado com este CPF/CNPJ."
        );
    }

    cliente.setNome(dto.nome());
    cliente.setEmail(dto.email());
    cliente.setTelefone(dto.telefone());
    cliente.setCpfCnpj(cpfCnpj);
    cliente.setEndereco(dto.endereco());

    Cliente atualizado = repository.save(cliente);

    return converterParaResponse(atualizado);
}

public void excluir(Long id) {

    Cliente cliente = repository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Cliente não encontrado"));

    long projetos = projetoRepository.countByClienteId(id);
    long assistencias = assistenciaTecnicaRepository.countByClienteId(id);

    if (projetos > 0 || assistencias > 0) {
        throw new RuntimeException(
                "Não é possível excluir o cliente. "
                + "Existem registros vinculados a este cliente."
        );
    }

    repository.delete(cliente);
}
}