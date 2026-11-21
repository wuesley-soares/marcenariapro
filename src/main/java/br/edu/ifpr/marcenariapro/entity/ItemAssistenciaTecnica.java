package br.edu.ifpr.marcenariapro.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_assistencia_tecnica")
public class ItemAssistenciaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @ManyToOne
    @JoinColumn(name = "assistencia_id")
    private AssistenciaTecnica assistencia;

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

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public AssistenciaTecnica getAssistencia() {
        return assistencia;
    }

    public void setAssistencia(AssistenciaTecnica assistencia) {
        this.assistencia = assistencia;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}