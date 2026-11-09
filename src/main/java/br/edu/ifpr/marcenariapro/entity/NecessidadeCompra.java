package br.edu.ifpr.marcenariapro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusNecessidadeCompra;
import jakarta.persistence.*;

@Entity
@Table(name = "necessidades_compra")
public class NecessidadeCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeNecessaria;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeAtendida = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNecessidadeCompra status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @OneToOne
    @JoinColumn(name = "item_producao_id", unique = true)
    private ItemProducao itemProducao;

    @OneToOne
    @JoinColumn(name = "item_assistencia_id", unique = true)
    private ItemAssistenciaTecnica itemAssistenciaTecnica;

    public NecessidadeCompra() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusNecessidadeCompra.ABERTA;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getQuantidadeNecessaria() {
        return quantidadeNecessaria;
    }

    public void setQuantidadeNecessaria(BigDecimal quantidadeNecessaria) {
        this.quantidadeNecessaria = quantidadeNecessaria;
    }

    public BigDecimal getQuantidadeAtendida() {
        return quantidadeAtendida;
    }

    public void setQuantidadeAtendida(BigDecimal quantidadeAtendida) {
        this.quantidadeAtendida = quantidadeAtendida;
    }

    public StatusNecessidadeCompra getStatus() {
        return status;
    }

    public void setStatus(StatusNecessidadeCompra status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public ItemProducao getItemProducao() {
        return itemProducao;
    }

    public void setItemProducao(ItemProducao itemProducao) {
        this.itemProducao = itemProducao;
    }

    public ItemAssistenciaTecnica getItemAssistenciaTecnica() {
        return itemAssistenciaTecnica;
    }

    public void setItemAssistenciaTecnica(
            ItemAssistenciaTecnica itemAssistenciaTecnica) {
        this.itemAssistenciaTecnica = itemAssistenciaTecnica;
    }
}