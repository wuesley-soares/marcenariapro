package br.edu.ifpr.marcenariapro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_assistencia")
public class ItemAssistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;

    private Double valorUnitario;

    @ManyToOne
    @JoinColumn(name = "assistencia_id")
    private AssistenciaTecnica assistencia;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    public Long getId() {
        return id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
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