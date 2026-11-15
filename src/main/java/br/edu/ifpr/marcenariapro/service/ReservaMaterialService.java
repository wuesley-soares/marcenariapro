package br.edu.ifpr.marcenariapro.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.marcenariapro.entity.ItemAssistenciaTecnica;
import br.edu.ifpr.marcenariapro.entity.ItemProducao;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.NecessidadeCompra;
import br.edu.ifpr.marcenariapro.entity.ReservaMaterial;
import br.edu.ifpr.marcenariapro.enums.StatusItemProducao;
import br.edu.ifpr.marcenariapro.enums.StatusNecessidadeCompra;
import br.edu.ifpr.marcenariapro.enums.StatusReservaMaterial;
import br.edu.ifpr.marcenariapro.repository.ItemProducaoRepository;
import br.edu.ifpr.marcenariapro.repository.NecessidadeCompraRepository;
import br.edu.ifpr.marcenariapro.repository.ReservaMaterialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservaMaterialService {

    private final ReservaMaterialRepository reservaRepository;
    private final NecessidadeCompraRepository necessidadeRepository;
    private final ItemProducaoRepository itemProducaoRepository;

    public ReservaMaterialService(
            ReservaMaterialRepository reservaRepository,
            NecessidadeCompraRepository necessidadeRepository,
            ItemProducaoRepository itemProducaoRepository) {

        this.reservaRepository = reservaRepository;
        this.necessidadeRepository = necessidadeRepository;
        this.itemProducaoRepository = itemProducaoRepository;
    }

    @Transactional
    public void reservarItemProducao(ItemProducao item) {

        if (reservaRepository.existsByItemProducaoId(item.getId())) {
            return;
        }

        criarReserva(
                item.getMaterial(),
                item.getQuantidade(),
                item,
                null
        );
    }

    @Transactional
    public void reservarItemAssistencia(ItemAssistenciaTecnica item) {

        if (reservaRepository.existsByItemAssistenciaTecnicaId(
                item.getId())) {
            return;
        }

        criarReserva(
                item.getMaterial(),
                BigDecimal.valueOf(item.getQuantidade()),
                null,
                item
        );
    }

    private void criarReserva(
            Material material,
            BigDecimal quantidadeSolicitada,
            ItemProducao itemProducao,
            ItemAssistenciaTecnica itemAssistencia) {

        if (quantidadeSolicitada == null
                || quantidadeSolicitada.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "A quantidade solicitada deve ser maior que zero."
            );
        }

        BigDecimal disponivel = calcularDisponivel(material);

        BigDecimal quantidadeReservada =
                disponivel.min(quantidadeSolicitada);

        BigDecimal quantidadeFaltante =
                quantidadeSolicitada.subtract(quantidadeReservada);

        ReservaMaterial reserva = new ReservaMaterial();

        reserva.setMaterial(material);
        reserva.setQuantidadeSolicitada(quantidadeSolicitada);
        reserva.setQuantidadeReservada(quantidadeReservada);
        reserva.setQuantidadeConsumida(BigDecimal.ZERO);
        reserva.setStatus(StatusReservaMaterial.ATIVA);
        reserva.setItemProducao(itemProducao);
        reserva.setItemAssistenciaTecnica(itemAssistencia);

        reservaRepository.save(reserva);

        if (quantidadeFaltante.compareTo(BigDecimal.ZERO) > 0) {
            criarNecessidadeCompra(
                    material,
                    quantidadeFaltante,
                    itemProducao,
                    itemAssistencia
            );
        }
    }

    public BigDecimal calcularDisponivel(Material material) {

        BigDecimal estoqueFisico =
                BigDecimal.valueOf(material.getQuantidade());

        List<ReservaMaterial> reservas =
                reservaRepository.findByMaterialIdAndStatus(
                        material.getId(),
                        StatusReservaMaterial.ATIVA
                );

        BigDecimal totalReservado = reservas.stream()
                .map(ReservaMaterial::getQuantidadeReservada)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return estoqueFisico.subtract(totalReservado)
                .max(BigDecimal.ZERO);
    }

    private void criarNecessidadeCompra(
            Material material,
            BigDecimal quantidadeFaltante,
            ItemProducao itemProducao,
            ItemAssistenciaTecnica itemAssistencia) {

        NecessidadeCompra necessidade = new NecessidadeCompra();

        necessidade.setMaterial(material);
        necessidade.setQuantidadeNecessaria(quantidadeFaltante);
        necessidade.setQuantidadeAtendida(BigDecimal.ZERO);
        necessidade.setStatus(StatusNecessidadeCompra.ABERTA);
        necessidade.setItemProducao(itemProducao);
        necessidade.setItemAssistenciaTecnica(itemAssistencia);

        necessidadeRepository.save(necessidade);
    }

    /**
     * Verifica se a reserva do item de produção está completa.
     */
    public boolean estaTotalmenteReservado(Long itemProducaoId) {

        ReservaMaterial reserva = reservaRepository
                .findByItemProducaoId(itemProducaoId)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva não encontrada para o item de produção."
                ));

        return reserva.getQuantidadeReservada()
                .compareTo(reserva.getQuantidadeSolicitada()) >= 0;
    }

    /**
     * Distribui a quantidade recebida entre as necessidades abertas
     * do material, priorizando as mais antigas.
     */
    @Transactional
    public void atenderNecessidadesCompra(
            Material material,
            BigDecimal quantidadeRecebida) {

        if (quantidadeRecebida == null
                || quantidadeRecebida.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

List<NecessidadeCompra> necessidades =
        necessidadeRepository.findByMaterialIdAndStatusIn(
                material.getId(),
                List.of(
                        StatusNecessidadeCompra.ABERTA,
                        StatusNecessidadeCompra.PARCIAL
                )
        );

        necessidades.sort(
                Comparator.comparing(NecessidadeCompra::getDataCriacao)
        );

        BigDecimal restante = quantidadeRecebida;

        for (NecessidadeCompra necessidade : necessidades) {

            if (restante.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            ReservaMaterial reserva =
                    buscarReservaDaNecessidade(necessidade);

            if (reserva.getStatus() != StatusReservaMaterial.ATIVA) {
                continue;
            }

            BigDecimal faltaNecessidade =
                    necessidade.getQuantidadeNecessaria()
                            .subtract(necessidade.getQuantidadeAtendida());

            BigDecimal capacidadeReserva =
                    reserva.getQuantidadeSolicitada()
                            .subtract(reserva.getQuantidadeReservada());

            BigDecimal quantidadeAtender = restante
                    .min(faltaNecessidade)
                    .min(capacidadeReserva);

            if (quantidadeAtender.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            reserva.setQuantidadeReservada(
                    reserva.getQuantidadeReservada().add(quantidadeAtender)
            );

            necessidade.setQuantidadeAtendida(
                    necessidade.getQuantidadeAtendida()
                            .add(quantidadeAtender)
            );

    boolean necessidadeCompleta =
        necessidade.getQuantidadeAtendida()
                .compareTo(necessidade.getQuantidadeNecessaria()) >= 0;

if (necessidadeCompleta) {
    necessidade.setStatus(StatusNecessidadeCompra.ATENDIDA);
} else {
    necessidade.setStatus(StatusNecessidadeCompra.PARCIAL);
}

            reservaRepository.save(reserva);
            necessidadeRepository.save(necessidade);

            /*
             * Quando a reserva do item de produção estiver completa,
             * libera o item para iniciar a produção.
             */
            if (necessidadeCompleta
                    && necessidade.getItemProducao() != null) {

                ItemProducao item = necessidade.getItemProducao();

                if (reserva.getQuantidadeReservada()
                        .compareTo(reserva.getQuantidadeSolicitada()) >= 0) {

                    item.setStatus(StatusItemProducao.LIBERADO);
                    itemProducaoRepository.save(item);
                }
            }

            restante = restante.subtract(quantidadeAtender);
        }
    }

    private ReservaMaterial buscarReservaDaNecessidade(
            NecessidadeCompra necessidade) {

        if (necessidade.getItemProducao() != null) {

            return reservaRepository.findByItemProducaoId(
                    necessidade.getItemProducao().getId()
            ).orElseThrow(() -> new RuntimeException(
                    "Reserva não encontrada para a necessidade de compra."
            ));
        }

        if (necessidade.getItemAssistenciaTecnica() != null) {

            return reservaRepository.findByItemAssistenciaTecnicaId(
                    necessidade.getItemAssistenciaTecnica().getId()
            ).orElseThrow(() -> new RuntimeException(
                    "Reserva não encontrada para a necessidade de compra."
            ));
        }

        throw new RuntimeException(
                "Necessidade de compra sem item de produção ou assistência."
        );
    }

    @Transactional
    public void liberarReservaProducao(Long itemProducaoId) {

        reservaRepository.findByItemProducaoId(itemProducaoId)
                .ifPresent(this::liberarReserva);

        necessidadeRepository.findByItemProducaoId(itemProducaoId)
                .ifPresent(necessidade -> {
                    necessidade.setStatus(StatusNecessidadeCompra.CANCELADA);
                    necessidadeRepository.save(necessidade);
                });
    }

    @Transactional
    public void liberarReservaAssistencia(Long itemAssistenciaId) {

        reservaRepository.findByItemAssistenciaTecnicaId(itemAssistenciaId)
                .ifPresent(this::liberarReserva);

        necessidadeRepository.findByItemAssistenciaTecnicaId(
                itemAssistenciaId
        ).ifPresent(necessidade -> {
            necessidade.setStatus(StatusNecessidadeCompra.CANCELADA);
            necessidadeRepository.save(necessidade);
        });
    }

    @Transactional
    public void removerReservaProducao(Long itemProducaoId) {

        necessidadeRepository.findByItemProducaoId(itemProducaoId)
                .ifPresent(necessidade ->
                        necessidadeRepository.delete(necessidade));

        reservaRepository.findByItemProducaoId(itemProducaoId)
                .ifPresent(reserva ->
                        reservaRepository.delete(reserva));
    }

    @Transactional
public void consumirReservaProducao(Long itemProducaoId) {

    ReservaMaterial reserva = reservaRepository
            .findByItemProducaoId(itemProducaoId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Reserva não encontrada para um dos materiais da produção."
            ));

    if (reserva.getStatus() != StatusReservaMaterial.ATIVA) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A reserva do material "
                        + reserva.getMaterial().getNome()
                        + " não está ativa."
        );
    }

    if (reserva.getQuantidadeReservada()
            .compareTo(reserva.getQuantidadeSolicitada()) < 0) {

        BigDecimal faltante = reserva.getQuantidadeSolicitada()
                .subtract(reserva.getQuantidadeReservada());

        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ainda faltam "
                        + faltante.stripTrailingZeros().toPlainString()
                        + " unidades do material "
                        + reserva.getMaterial().getNome()
                        + " para iniciar a produção."
        );
    }

    reserva.setQuantidadeConsumida(
            reserva.getQuantidadeSolicitada()
    );

    reserva.setStatus(StatusReservaMaterial.CONSUMIDA);

    reservaRepository.save(reserva);
}
    private void liberarReserva(ReservaMaterial reserva) {

        if (reserva.getStatus() == StatusReservaMaterial.CONSUMIDA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível liberar uma reserva já consumida."
            );
        }

        reserva.setStatus(StatusReservaMaterial.LIBERADA);
        reserva.setQuantidadeReservada(BigDecimal.ZERO);

        reservaRepository.save(reserva);
    }

    @Transactional
    public void consumirReservaAssistencia(Long itemAssistenciaId) {

        ReservaMaterial reserva = reservaRepository
                .findByItemAssistenciaTecnicaId(itemAssistenciaId)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva não encontrada para o material da assistência técnica."
                ));

        if (reserva.getStatus() != StatusReservaMaterial.ATIVA) {
            throw new RuntimeException(
                    "A reserva do material da assistência não está ativa."
            );
        }

        if (reserva.getQuantidadeReservada()
                .compareTo(reserva.getQuantidadeSolicitada()) < 0) {

            throw new RuntimeException(
                    "Ainda não há quantidade suficiente reservada para o material: "
                            + reserva.getMaterial().getNome()
            );
        }

        reserva.setQuantidadeConsumida(
                reserva.getQuantidadeSolicitada()
        );

        reserva.setStatus(StatusReservaMaterial.CONSUMIDA);

        reservaRepository.save(reserva);
    }

    @Transactional
    public void removerReservaAssistencia(Long itemAssistenciaId) {

        necessidadeRepository.findByItemAssistenciaTecnicaId(itemAssistenciaId)
                .ifPresent(necessidade ->
                        necessidadeRepository.delete(necessidade));

        reservaRepository.findByItemAssistenciaTecnicaId(itemAssistenciaId)
                .ifPresent(reserva ->
                        reservaRepository.delete(reserva));
    }

    public BigDecimal calcularQuantidadeFaltante(Long itemProducaoId) {

    ReservaMaterial reserva = reservaRepository
            .findByItemProducaoId(itemProducaoId)
            .orElseThrow(() -> new RuntimeException(
                    "Reserva não encontrada para o item de produção."
            ));

    return reserva.getQuantidadeSolicitada()
            .subtract(reserva.getQuantidadeReservada())
            .max(BigDecimal.ZERO);
}
}