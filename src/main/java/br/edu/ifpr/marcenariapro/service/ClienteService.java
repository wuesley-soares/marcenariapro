
package br.edu.ifpr.marcenariapro.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.marcenariapro.dto.AmbienteClienteDTO;
import br.edu.ifpr.marcenariapro.dto.ClienteDetalheDTO;
import br.edu.ifpr.marcenariapro.dto.FinanceiroClienteDTO;
import br.edu.ifpr.marcenariapro.dto.ProjetoClienteDTO;

import br.edu.ifpr.marcenariapro.entity.Ambiente;
import br.edu.ifpr.marcenariapro.entity.Orcamento;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.entity.Pagamento;
import br.edu.ifpr.marcenariapro.entity.Projeto;

import br.edu.ifpr.marcenariapro.enums.StatusPedido;
import br.edu.ifpr.marcenariapro.enums.StatusPagamento;

import br.edu.ifpr.marcenariapro.repository.AmbienteRepository;
import br.edu.ifpr.marcenariapro.repository.OrcamentoRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.PagamentoRepository;

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
    private final AmbienteRepository ambienteRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;

public ClienteService(
        ClienteRepository repository,
        ProjetoRepository projetoRepository,
        AssistenciaTecnicaRepository assistenciaTecnicaRepository,
        AmbienteRepository ambienteRepository,
        OrcamentoRepository orcamentoRepository,
        PedidoRepository pedidoRepository,
        PagamentoRepository pagamentoRepository) {

    this.repository = repository;
    this.projetoRepository = projetoRepository;
    this.assistenciaTecnicaRepository = assistenciaTecnicaRepository;
    this.ambienteRepository = ambienteRepository;
    this.orcamentoRepository = orcamentoRepository;
    this.pedidoRepository = pedidoRepository;
    this.pagamentoRepository = pagamentoRepository;
}

    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {

        validarDados(dto);

        String email = dto.email().trim().toLowerCase();
        String cpfCnpj = limparDocumento(dto.cpfCnpj());

        if (repository.findByEmail(email).isPresent()) {
            throw new RuntimeException(
                    "Já existe um cliente cadastrado com este e-mail.");
        }

        if (repository.findByCpfCnpj(cpfCnpj).isPresent()) {
            throw new RuntimeException(
                    "Já existe um cliente cadastrado com este CPF/CNPJ.");
        }

        Cliente cliente = new Cliente();

        preencherCliente(cliente, dto, email, cpfCnpj);

        return converterParaResponse(repository.save(cliente));
    }

    public List<ClienteResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado."));

        return converterParaResponse(cliente);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {

        Cliente cliente = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado."));

        validarDados(dto);

        String email = dto.email().trim().toLowerCase();
        String cpfCnpj = limparDocumento(dto.cpfCnpj());

        Optional<Cliente> clienteEmail =
                repository.findByEmail(email);

        if (clienteEmail.isPresent()
                && !clienteEmail.get().getId().equals(id)) {
            throw new RuntimeException(
                    "Já existe outro cliente cadastrado com este e-mail.");
        }

        Optional<Cliente> clienteCpfCnpj =
                repository.findByCpfCnpj(cpfCnpj);

        if (clienteCpfCnpj.isPresent()
                && !clienteCpfCnpj.get().getId().equals(id)) {
            throw new RuntimeException(
                    "Já existe outro cliente cadastrado com este CPF/CNPJ.");
        }

        preencherCliente(cliente, dto, email, cpfCnpj);

        return converterParaResponse(repository.save(cliente));
    }

    private void validarDados(ClienteRequestDTO dto) {

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new RuntimeException(
                    "O nome do cliente é obrigatório.");
        }

        String nome = dto.nome().trim();

        if (nome.length() < 2 || nome.length() > 100
                || !nome.matches(
                    "[\\p{L}][\\p{L} '\\u002D]*[\\p{L}]|[\\p{L}]")) {
            throw new RuntimeException(
                    "Informe um nome válido, usando letras e espaços.");
        }

        if (dto.email() == null || dto.email().isBlank()) {
            throw new RuntimeException(
                    "O e-mail do cliente é obrigatório.");
        }

        String email = dto.email().trim();

        if (email.length() > 254
                || !email.matches(
                    "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+"
                    + "@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}"
                    + "[A-Za-z0-9])?"
                    + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}"
                    + "[A-Za-z0-9])?)+$")) {
            throw new RuntimeException(
                    "O e-mail informado é inválido.");
        }

        if (dto.telefone() == null || dto.telefone().isBlank()) {
            throw new RuntimeException(
                    "O telefone do cliente é obrigatório.");
        }

        String telefone = dto.telefone().replaceAll("\\D", "");

        if (telefone.length() < 10 || telefone.length() > 11
                || telefone.matches("(\\d)\\1+")
                || !telefone.matches("[1-9][0-9]{9,10}")) {
            throw new RuntimeException(
                    "Informe um telefone válido com DDD.");
        }

        if (dto.cpfCnpj() == null || dto.cpfCnpj().isBlank()) {
            throw new RuntimeException(
                    "O CPF/CNPJ é obrigatório.");
        }

        String documento = limparDocumento(dto.cpfCnpj());

        if (!validarCpfCnpj(documento)) {
            throw new RuntimeException(
                    "O CPF/CNPJ informado é inválido.");
        }
    }

    private String limparDocumento(String documento) {
        return documento.replaceAll("\\D", "");
    }

    private boolean validarCpfCnpj(String documento) {
        if (documento.length() == 11) {
            return validarCpf(documento);
        }

        if (documento.length() == 14) {
            return validarCnpj(documento);
        }

        return false;
    }

    private boolean validarCpf(String cpf) {

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int resto = (soma * 10) % 11;
        int digito1 = resto == 10 ? 0 : resto;

        if (digito1 != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        resto = (soma * 10) % 11;
        int digito2 = resto == 10 ? 0 : resto;

        return digito2 == Character.getNumericValue(cpf.charAt(10));
    }

    private boolean validarCnpj(String cnpj) {

        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;

        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
        }

        int resto = soma % 11;
        int digito1 = resto < 2 ? 0 : 11 - resto;

        if (digito1 != Character.getNumericValue(cnpj.charAt(12))) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
        }

        resto = soma % 11;
        int digito2 = resto < 2 ? 0 : 11 - resto;

        return digito2 == Character.getNumericValue(cnpj.charAt(13));
    }

    private void preencherCliente(
            Cliente cliente,
            ClienteRequestDTO dto,
            String email,
            String cpfCnpj) {

        cliente.setNome(dto.nome().trim());
        cliente.setEmail(email);
        cliente.setTelefone(dto.telefone().trim());
        cliente.setCpfCnpj(cpfCnpj);
        cliente.setEndereco(dto.endereco());
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
    @Transactional(readOnly = true)
public ClienteDetalheDTO buscarDetalhes(Long clienteId) {

    Cliente cliente = repository.findById(clienteId)
            .orElseThrow(() ->
                    new RuntimeException("Cliente não encontrado."));

    List<Projeto> projetos =
            projetoRepository.findByClienteId(clienteId);

    List<ProjetoClienteDTO> projetosDTO = new ArrayList<>();

    for (Projeto projeto : projetos) {

        List<Ambiente> ambientes =
                ambienteRepository.findByProjetoId(projeto.getId());

        List<AmbienteClienteDTO> ambientesDTO = new ArrayList<>();

        for (Ambiente ambiente : ambientes) {

            Orcamento orcamento = orcamentoRepository
                    .findByAmbienteId(ambiente.getId())
                    .orElse(null);

            BigDecimal valorOrcamento = BigDecimal.ZERO;
            String statusOrcamento = null;

            Long pedidoId = null;
            String statusPedido = null;

            BigDecimal valorPago = BigDecimal.ZERO;
            BigDecimal saldoRestante = BigDecimal.ZERO;
            String situacaoFinanceira = "SEM_PEDIDO";

            if (orcamento != null) {

                valorOrcamento = orcamento.getValor() != null
                        ? orcamento.getValor()
                        : BigDecimal.ZERO;

                statusOrcamento = orcamento.getStatus() != null
                        ? orcamento.getStatus().name()
                        : null;

                Pedido pedido = pedidoRepository
                        .findByOrcamentoId(orcamento.getId())
                        .orElse(null);

                if (pedido != null) {

                    pedidoId = pedido.getId();

                    statusPedido = pedido.getStatus() != null
                            ? pedido.getStatus().name()
                            : null;

                    valorPago = somarPagamentosPagos(pedido);

                    if (pedido.getStatus() == StatusPedido.CANCELADO) {

                        situacaoFinanceira = "CANCELADO";

                    } else {

                        saldoRestante = valorOrcamento.subtract(valorPago);

                        if (saldoRestante.compareTo(BigDecimal.ZERO) < 0) {
                            saldoRestante = BigDecimal.ZERO;
                        }

                        situacaoFinanceira =
                                determinarSituacaoFinanceira(
                                        valorPago,
                                        saldoRestante);
                    }
                }
            }

            ambientesDTO.add(new AmbienteClienteDTO(
                    ambiente.getId(),
                    ambiente.getNome(),
                    valorOrcamento,
                    statusOrcamento,
                    pedidoId,
                    statusPedido,
                    valorPago,
                    saldoRestante,
                    situacaoFinanceira
            ));
        }

        projetosDTO.add(new ProjetoClienteDTO(
                projeto.getId(),
                projeto.getNome(),
                ambientesDTO
        ));
    }

    FinanceiroClienteDTO financeiro = calcularFinanceiro(clienteId);

    return new ClienteDetalheDTO(
            converterParaResponse(cliente),
            financeiro,
            projetosDTO
    );
}

@Transactional(readOnly = true)
public FinanceiroClienteDTO buscarFinanceiro(Long clienteId) {

    if (!repository.existsById(clienteId)) {
        throw new RuntimeException("Cliente não encontrado.");
    }

    return calcularFinanceiro(clienteId);
}

private FinanceiroClienteDTO calcularFinanceiro(Long clienteId) {

    BigDecimal totalPedidos = BigDecimal.ZERO;
    BigDecimal totalPago = BigDecimal.ZERO;

    long quantidadePedidos = 0;

    List<Projeto> projetos =
            projetoRepository.findByClienteId(clienteId);

    for (Projeto projeto : projetos) {

        List<Ambiente> ambientes =
                ambienteRepository.findByProjetoId(projeto.getId());

        for (Ambiente ambiente : ambientes) {

            Orcamento orcamento = orcamentoRepository
                    .findByAmbienteId(ambiente.getId())
                    .orElse(null);

            if (orcamento == null) {
                continue;
            }

            Pedido pedido = pedidoRepository
                    .findByOrcamentoId(orcamento.getId())
                    .orElse(null);

            if (pedido == null
                    || pedido.getStatus() == StatusPedido.CANCELADO) {
                continue;
            }

            BigDecimal valorPedido = orcamento.getValor() != null
                    ? orcamento.getValor()
                    : BigDecimal.ZERO;

            totalPedidos = totalPedidos.add(valorPedido);

            totalPago = totalPago.add(
                    somarPagamentosPagos(pedido));

            quantidadePedidos++;
        }
    }

    BigDecimal saldoReceber = totalPedidos.subtract(totalPago);

    if (saldoReceber.compareTo(BigDecimal.ZERO) < 0) {
        saldoReceber = BigDecimal.ZERO;
    }

    String situacao;

    if (quantidadePedidos == 0) {
        situacao = "SEM_PEDIDOS";
    } else if (saldoReceber.compareTo(BigDecimal.ZERO) == 0) {
        situacao = "QUITADO";
    } else {
        situacao = "COM_SALDO_A_RECEBER";
    }

    return new FinanceiroClienteDTO(
            totalPedidos,
            totalPago,
            saldoReceber,
            quantidadePedidos,
            situacao
    );
}

private BigDecimal somarPagamentosPagos(Pedido pedido) {

    List<Pagamento> pagamentos =
            pagamentoRepository.findByPedidoId(pedido.getId());

    return pagamentos.stream()
            .filter(p -> p.getStatus() == StatusPagamento.PAGO)
            .map(Pagamento::getValor)
            .filter(valor -> valor != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

private String determinarSituacaoFinanceira(
        BigDecimal valorPago,
        BigDecimal saldoRestante) {

    if (saldoRestante.compareTo(BigDecimal.ZERO) == 0) {
        return "QUITADO";
    }

    if (valorPago.compareTo(BigDecimal.ZERO) > 0) {
        return "PARCIALMENTE_PAGO";
    }

    return "PENDENTE";
}

    public void excluir(Long id) {

        Cliente cliente = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado."));

        long projetos = projetoRepository.countByClienteId(id);
        long assistencias =
                assistenciaTecnicaRepository.countByClienteId(id);

        if (projetos > 0 || assistencias > 0) {
            throw new RuntimeException(
                    "Não é possível excluir o cliente. "
                    + "Existem registros vinculados a este cliente.");
        }

        repository.delete(cliente);
    }
}
