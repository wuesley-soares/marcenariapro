package br.edu.ifpr.marcenariapro.dto;

public record RelatorioResumoResponseDTO(
        long totalClientes,
        long totalProjetos,
        long totalOrcamentos,
        long totalPedidos,
        long pedidosEmProducao,
        long pedidosConcluidos,
        long totalAssistencias,
        double valorTotalVendas,
        double valorTotalRecebido
) {}