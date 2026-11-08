package br.edu.ifpr.marcenariapro.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nomeFantasia;

    @Column(length = 150)
    private String razaoSocial;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String complemento;

    @Column(nullable = false, length = 11)
    private String telefone1;

    @Column(length = 11)
    private String telefone2;

    @Column(length = 11)
    private String celular;

    @Column(nullable = false, length = 20)
    private String whatsappTipo;

    private LocalDateTime dataCadastro;

    @OneToMany(
        mappedBy = "fornecedor",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<VendedorFornecedor> vendedores = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        dataCadastro = LocalDateTime.now();
    }

    public void adicionarVendedor(VendedorFornecedor vendedor) {
        vendedores.add(vendedor);
        vendedor.setFornecedor(this);
    }

    public void limparVendedores() {
        vendedores.forEach(v -> v.setFornecedor(null));
        vendedores.clear();
    }

    public Long getId() { return id; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getTelefone1() { return telefone1; }
    public void setTelefone1(String telefone1) { this.telefone1 = telefone1; }
    public String getTelefone2() { return telefone2; }
    public void setTelefone2(String telefone2) { this.telefone2 = telefone2; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getWhatsappTipo() { return whatsappTipo; }
    public void setWhatsappTipo(String whatsappTipo) { this.whatsappTipo = whatsappTipo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public List<VendedorFornecedor> getVendedores() { return vendedores; }
}