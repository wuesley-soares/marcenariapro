package br.edu.ifpr.marcenariapro.entity;

import java.time.LocalDateTime;

import br.edu.ifpr.marcenariapro.enums.EtapaImagemAssistencia;
import jakarta.persistence.*;

@Entity
@Table(name = "imagens_assistencia_tecnica")
public class ImagemAssistenciaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeOriginal;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String caminhoArquivo;

    @Column(nullable = false)
    private String tipoConteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtapaImagemAssistencia etapa;

    @Column(nullable = false)
    private LocalDateTime dataUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistencia_id", nullable = false)
    private AssistenciaTecnica assistencia;

    @PrePersist
    public void prePersist() {
        if (dataUpload == null) {
            dataUpload = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public EtapaImagemAssistencia getEtapa() {
        return etapa;
    }

    public void setEtapa(EtapaImagemAssistencia etapa) {
        this.etapa = etapa;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public AssistenciaTecnica getAssistencia() {
        return assistencia;
    }

    public void setAssistencia(AssistenciaTecnica assistencia) {
        this.assistencia = assistencia;
    }
}