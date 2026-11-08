package br.edu.ifpr.marcenariapro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.marcenariapro.dto.*;
import br.edu.ifpr.marcenariapro.entity.Fornecedor;
import br.edu.ifpr.marcenariapro.entity.VendedorFornecedor;
import br.edu.ifpr.marcenariapro.repository.FornecedorRepository;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    public FornecedorService(FornecedorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FornecedorResponseDTO salvar(FornecedorRequestDTO dto) {
        validarDados(dto);

        String cnpj = limparNumeros(dto.cnpj());

        if (repository.findByCnpj(cnpj).isPresent()) {
            throw new RuntimeException(
                "Já existe um fornecedor cadastrado com este CNPJ."
            );
        }

        Fornecedor fornecedor = new Fornecedor();

        preencherFornecedor(fornecedor, dto, cnpj);
        atualizarVendedores(fornecedor, dto.vendedores());

        return converterParaResponse(repository.save(fornecedor));
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listar() {
        return repository.findAll()
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Long id) {
        Fornecedor fornecedor = buscarEntidade(id);
        return converterParaResponse(fornecedor);
    }

    @Transactional
    public FornecedorResponseDTO atualizar(
            Long id,
            FornecedorRequestDTO dto) {

        validarDados(dto);

        Fornecedor fornecedor = buscarEntidade(id);
        String cnpj = limparNumeros(dto.cnpj());

        Optional<Fornecedor> existente = repository.findByCnpj(cnpj);

        if (existente.isPresent()
                && !existente.get().getId().equals(id)) {
            throw new RuntimeException(
                "Já existe outro fornecedor cadastrado com este CNPJ."
            );
        }

        preencherFornecedor(fornecedor, dto, cnpj);
        atualizarVendedores(fornecedor, dto.vendedores());

        return converterParaResponse(repository.save(fornecedor));
    }

    @Transactional
    public void excluir(Long id) {
        Fornecedor fornecedor = buscarEntidade(id);
        repository.delete(fornecedor);
    }

    private Fornecedor buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Fornecedor não encontrado.")
            );
    }

    private void validarDados(FornecedorRequestDTO dto) {

        if (dto.nomeFantasia() == null
                || dto.nomeFantasia().isBlank()) {
            throw new RuntimeException(
                "O nome fantasia é obrigatório."
            );
        }

        if (dto.nomeFantasia().trim().length() > 150) {
            throw new RuntimeException(
                "O nome fantasia deve ter no máximo 150 caracteres."
            );
        }

        if (dto.razaoSocial() != null
                && dto.razaoSocial().trim().length() > 150) {
            throw new RuntimeException(
                "A razão social deve ter no máximo 150 caracteres."
            );
        }

        String cnpj = limparNumeros(dto.cnpj());

        if (!validarCnpj(cnpj)) {
            throw new RuntimeException("O CNPJ informado é inválido.");
        }

        validarTelefone(dto.telefone1(), true, "Telefone 1");
        validarTelefone(dto.telefone2(), false, "Telefone 2");
        validarTelefone(dto.celular(), false, "Celular");

        String tipo = dto.whatsappTipo();

        if (tipo == null || tipo.isBlank()) {
            throw new RuntimeException(
                "Selecione qual número do fornecedor possui WhatsApp."
            );
        }

        String numeroWhatsApp = switch (tipo) {
            case "TELEFONE_1" -> dto.telefone1();
            case "TELEFONE_2" -> dto.telefone2();
            case "CELULAR" -> dto.celular();
            default -> throw new RuntimeException(
                "A opção de WhatsApp selecionada é inválida."
            );
        };

        if (numeroWhatsApp == null || numeroWhatsApp.isBlank()) {
            throw new RuntimeException(
                "O número selecionado para WhatsApp precisa estar preenchido."
            );
        }

        if (dto.vendedores() != null) {
            for (VendedorFornecedorRequestDTO vendedor : dto.vendedores()) {
                if (vendedor.nome() == null || vendedor.nome().isBlank()) {
                    throw new RuntimeException(
                        "Informe o nome de todos os vendedores cadastrados."
                    );
                }

                if (vendedor.nome().trim().length() > 120) {
                    throw new RuntimeException(
                        "O nome do vendedor deve ter no máximo 120 caracteres."
                    );
                }

                validarTelefone(
                    vendedor.telefone(),
                    true,
                    "Telefone do vendedor"
                );
            }
        }
    }

    private void validarTelefone(
            String telefone,
            boolean obrigatorio,
            String campo) {

        if (telefone == null || telefone.isBlank()) {
            if (obrigatorio) {
                throw new RuntimeException(campo + " é obrigatório.");
            }
            return;
        }

        String numero = limparNumeros(telefone);

        if (numero.length() < 10
                || numero.length() > 11
                || numero.matches("(\\d)\\1+")
                || !numero.matches("[1-9][0-9]{9,10}")) {
            throw new RuntimeException(
                campo + " inválido. Informe DDD e número."
            );
        }
    }

    private String limparNumeros(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private boolean validarCnpj(String cnpj) {

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
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

    private void preencherFornecedor(
            Fornecedor fornecedor,
            FornecedorRequestDTO dto,
            String cnpj) {

        fornecedor.setNomeFantasia(dto.nomeFantasia().trim());

        fornecedor.setRazaoSocial(
            dto.razaoSocial() == null || dto.razaoSocial().isBlank()
                ? null : dto.razaoSocial().trim()
        );

        fornecedor.setCnpj(cnpj);
        fornecedor.setCep(limparNumeros(dto.cep()));
        fornecedor.setEstado(dto.estado());
        fornecedor.setCidade(dto.cidade());
        fornecedor.setBairro(dto.bairro());
        fornecedor.setRua(dto.rua());
        fornecedor.setNumero(dto.numero());
        fornecedor.setComplemento(dto.complemento());

        fornecedor.setTelefone1(limparNumeros(dto.telefone1()));
        fornecedor.setTelefone2(
            dto.telefone2() == null || dto.telefone2().isBlank()
                ? null : limparNumeros(dto.telefone2())
        );
        fornecedor.setCelular(
            dto.celular() == null || dto.celular().isBlank()
                ? null : limparNumeros(dto.celular())
        );

        fornecedor.setWhatsappTipo(dto.whatsappTipo());
    }

    private void atualizarVendedores(
            Fornecedor fornecedor,
            List<VendedorFornecedorRequestDTO> vendedoresDTO) {

        fornecedor.limparVendedores();

        if (vendedoresDTO == null) {
            return;
        }

        for (VendedorFornecedorRequestDTO dto : vendedoresDTO) {
            VendedorFornecedor vendedor = new VendedorFornecedor();

            vendedor.setNome(dto.nome().trim());
            vendedor.setTelefone(limparNumeros(dto.telefone()));

            fornecedor.adicionarVendedor(vendedor);
        }
    }

    private FornecedorResponseDTO converterParaResponse(
            Fornecedor fornecedor) {

        List<VendedorFornecedorResponseDTO> vendedores =
            fornecedor.getVendedores()
                .stream()
                .map(v -> new VendedorFornecedorResponseDTO(
                    v.getId(),
                    v.getNome(),
                    v.getTelefone()
                ))
                .toList();

        return new FornecedorResponseDTO(
            fornecedor.getId(),
            fornecedor.getNomeFantasia(),
            fornecedor.getRazaoSocial(),
            fornecedor.getCnpj(),
            fornecedor.getCep(),
            fornecedor.getEstado(),
            fornecedor.getCidade(),
            fornecedor.getBairro(),
            fornecedor.getRua(),
            fornecedor.getNumero(),
            fornecedor.getComplemento(),
            fornecedor.getTelefone1(),
            fornecedor.getTelefone2(),
            fornecedor.getCelular(),
            fornecedor.getWhatsappTipo(),
            fornecedor.getDataCadastro(),
            vendedores
        );
    }
}