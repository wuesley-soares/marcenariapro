package br.edu.ifpr.marcenariapro.entity;

import java.math.BigDecimal;

import br.edu.ifpr.marcenariapro.enums.StatusItemProducao;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_producao")
public class ItemProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    private StatusItemProducao status;

    @ManyToOne
    @JoinColumn(name = "producao_id")
    private Producao producao;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    public Long getId() {
        return id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public StatusItemProducao getStatus() {
        return status;
    }

    public void setStatus(StatusItemProducao status) {
        this.status = status;
    }

    public Producao getProducao() {
        return producao;
    }

    public void setProducao(Producao producao) {
        this.producao = producao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}