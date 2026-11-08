package br.edu.ifpr.marcenariapro.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FornecedorResponseDTO(
    Long id,
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
    LocalDateTime dataCadastro,
    List<VendedorFornecedorResponseDTO> vendedores
) {}