package br.edu.ifpr.marcenariapro.entity;

import br.edu.ifpr.marcenariapro.enums.StatusSubstituicao;
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
@Table(name = "solicitacoes_substituicao")
public class SolicitacaoSubstituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusSubstituicao status;

    @ManyToOne
    @JoinColumn(name = "item_producao_id")
    private ItemProducao itemProducao;

    @ManyToOne
    @JoinColumn(name = "novo_material_id")
    private Material novoMaterial;

    public Long getId() {
        return id;
    }

    public StatusSubstituicao getStatus() {
        return status;
    }

    public void setStatus(StatusSubstituicao status) {
        this.status = status;
    }

    public ItemProducao getItemProducao() {
        return itemProducao;
    }

    public void setItemProducao(ItemProducao itemProducao) {
        this.itemProducao = itemProducao;
    }

    public Material getNovoMaterial() {
        return novoMaterial;
    }

    public void setNovoMaterial(Material novoMaterial) {
        this.novoMaterial = novoMaterial;
    }
}