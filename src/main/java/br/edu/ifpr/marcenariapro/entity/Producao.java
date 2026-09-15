package br.edu.ifpr.marcenariapro.entity;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpr.marcenariapro.enums.StatusProducao;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "producoes")
public class Producao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusProducao status;

    private LocalDateTime dataCriacao;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @OneToMany(mappedBy = "producao")
    private List<ItemProducao> itens;

    public Producao() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public StatusProducao getStatus() {
        return status;
    }

    public void setStatus(StatusProducao status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public List<ItemProducao> getItens() {
    return itens;
    }

    public void setItens(List<ItemProducao> itens) {
    this.itens = itens;
    }
}