package br.edu.ifpr.marcenariapro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusReservaMaterial;
import jakarta.persistence.*;

@Entity
@Table(name = "reservas_materiais")
public class ReservaMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeSolicitada;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeReservada = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeConsumida = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReservaMaterial status;

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

    public ReservaMaterial() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusReservaMaterial.ATIVA;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(BigDecimal quantidadeSolicitada) {
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public BigDecimal getQuantidadeReservada() {
        return quantidadeReservada;
    }

    public void setQuantidadeReservada(BigDecimal quantidadeReservada) {
        this.quantidadeReservada = quantidadeReservada;
    }

    public BigDecimal getQuantidadeConsumida() {
        return quantidadeConsumida;
    }

    public void setQuantidadeConsumida(BigDecimal quantidadeConsumida) {
        this.quantidadeConsumida = quantidadeConsumida;
    }

    public StatusReservaMaterial getStatus() {
        return status;
    }

    public void setStatus(StatusReservaMaterial status) {
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