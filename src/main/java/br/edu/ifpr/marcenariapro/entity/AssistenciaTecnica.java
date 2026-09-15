package br.edu.ifpr.marcenariapro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.StatusAssistencia;
import jakarta.persistence.*;

@Entity
@Table(name = "assistencias_tecnicas")
public class AssistenciaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataSolicitacao;

    private LocalDateTime dataAgendamento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private LocalDate dataEntregaOriginal;

    private Boolean dentroGarantia;

    private Double valorServico;

    private Double valorMateriais;

    private Double valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusAssistencia status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Usuario funcionario;

    public Long getId() {
        return id;
    }

    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDate dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataEntregaOriginal() {
        return dataEntregaOriginal;
    }

    public void setDataEntregaOriginal(LocalDate dataEntregaOriginal) {
        this.dataEntregaOriginal = dataEntregaOriginal;
    }

    public Boolean getDentroGarantia() {
        return dentroGarantia;
    }

    public void setDentroGarantia(Boolean dentroGarantia) {
        this.dentroGarantia = dentroGarantia;
    }

    public Double getValorServico() {
        return valorServico;
    }

    public void setValorServico(Double valorServico) {
        this.valorServico = valorServico;
    }

    public Double getValorMateriais() {
        return valorMateriais;
    }

    public void setValorMateriais(Double valorMateriais) {
        this.valorMateriais = valorMateriais;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusAssistencia getStatus() {
        return status;
    }

    public void setStatus(StatusAssistencia status) {
        this.status = status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Usuario funcionario) {
        this.funcionario = funcionario;
    }
}