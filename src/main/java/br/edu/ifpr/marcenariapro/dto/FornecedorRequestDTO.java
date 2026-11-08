package br.edu.ifpr.marcenariapro.dto;

import java.util.List;

public record FornecedorRequestDTO(
    String nomeFantasia,
    String razaoSocial,
    String cnpj,
    String cep,
    String estado,
    String cidade,
    String bairro,
    String rua,
    String numero,
    String complemento,
    String telefone1,
    String telefone2,
    String celular,
    String whatsappTipo,
    List<VendedorFornecedorRequestDTO> vendedores
) {}