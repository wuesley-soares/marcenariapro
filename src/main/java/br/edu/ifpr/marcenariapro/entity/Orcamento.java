package br.edu.ifpr.marcenariapro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusOrcamento;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;

    private String descricao;

    private BigDecimal valorMaoObra;

    private BigDecimal margemLucro;

    @Enumerated(EnumType.STRING)
    private StatusOrcamento status;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataValidade;

    @OneToOne
    @JoinColumn(name = "ambiente_id")
    private Ambiente ambiente;

   public Orcamento() {
    this.dataCriacao = LocalDateTime.now();
    this.dataValidade = this.dataCriacao.plusDays(90);
}

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusOrcamento getStatus() {
        return status;
    }

    public void setStatus(StatusOrcamento status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Ambiente getAmbiente() {
    return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
    this.ambiente = ambiente;
    }

    public LocalDateTime getDataValidade() {
    return dataValidade;
}

public void setDataValidade(LocalDateTime dataValidade) {
    this.dataValidade = dataValidade;
}

public BigDecimal getValorMaoObra() {
    return valorMaoObra;
}

public void setValorMaoObra(BigDecimal valorMaoObra) {
    this.valorMaoObra = valorMaoObra;
}

public BigDecimal getMargemLucro() {
    return margemLucro;
}

public void setMargemLucro(BigDecimal margemLucro) {
    this.margemLucro = margemLucro;
}


}