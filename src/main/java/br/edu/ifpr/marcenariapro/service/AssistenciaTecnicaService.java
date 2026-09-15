package br.edu.ifpr.marcenariapro.service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.AssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaRequestDTO;
import br.edu.ifpr.marcenariapro.dto.ItemAssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.entity.AssistenciaTecnica;
import br.edu.ifpr.marcenariapro.entity.Cliente;
import br.edu.ifpr.marcenariapro.entity.ItemAssistenciaTecnica;
import br.edu.ifpr.marcenariapro.entity.Material;
import br.edu.ifpr.marcenariapro.entity.Pedido;
import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.repository.AssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.ClienteRepository;
import br.edu.ifpr.marcenariapro.repository.ItemAssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.MaterialRepository;
import br.edu.ifpr.marcenariapro.repository.PedidoRepository;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;


@Service
public class AssistenciaTecnicaService {

    private final AssistenciaTecnicaRepository assistenciaRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemAssistenciaTecnicaRepository itemAssistenciaRepository;
    private final MaterialRepository materialRepository;

public AssistenciaTecnicaService(
        AssistenciaTecnicaRepository assistenciaRepository,
        ClienteRepository clienteRepository,
        PedidoRepository pedidoRepository,
        UsuarioRepository usuarioRepository,
        ItemAssistenciaTecnicaRepository itemAssistenciaRepository,
        MaterialRepository materialRepository) {

    this.assistenciaRepository = assistenciaRepository;
    this.clienteRepository = clienteRepository;
    this.pedidoRepository = pedidoRepository;
    this.usuarioRepository = usuarioRepository;
    this.itemAssistenciaRepository = itemAssistenciaRepository;
    this.materialRepository = materialRepository;
}
    public AssistenciaTecnicaResponseDTO salvar(
            AssistenciaTecnicaRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        Usuario funcionario = usuarioRepository.findById(dto.funcionarioId())
                .orElseThrow(() ->
                        new RuntimeException("Funcionário não encontrado"));

        AssistenciaTecnica assistencia = new AssistenciaTecnica();

        assistencia.setDataSolicitacao(dto.dataSolicitacao());
        assistencia.setDataAgendamento(dto.dataAgendamento());
        assistencia.setDescricao(dto.descricao());
        assistencia.setDataEntregaOriginal(dto.dataEntregaOriginal());

        assistencia.setValorServico(dto.valorServico());
        assistencia.setValorMateriais(dto.valorMateriais());

        assistencia.setStatus(dto.status());

        assistencia.setCliente(cliente);
        assistencia.setPedido(pedido);
        assistencia.setFuncionario(funcionario);

        verificarGarantia(assistencia);

        AssistenciaTecnica salva =
                assistenciaRepository.save(assistencia);

        return converterParaResponse(salva);
    }

    public List<AssistenciaTecnicaResponseDTO> listar() {

        return assistenciaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public AssistenciaTecnicaResponseDTO buscarPorId(Long id) {

        AssistenciaTecnica assistencia =
                assistenciaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assistência técnica não encontrada"));

        return converterParaResponse(assistencia);
    }

    public AssistenciaTecnicaResponseDTO atualizar(
        Long id,
        AssistenciaTecnicaRequestDTO dto) {

    AssistenciaTecnica assistencia =
            assistenciaRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Assistência técnica não encontrada"));

    Cliente cliente = clienteRepository.findById(dto.clienteId())
            .orElseThrow(() ->
                    new RuntimeException("Cliente não encontrado"));

    Pedido pedido = pedidoRepository.findById(dto.pedidoId())
            .orElseThrow(() ->
                    new RuntimeException("Pedido não encontrado"));

    Usuario funcionario = usuarioRepository.findById(dto.funcionarioId())
            .orElseThrow(() ->
                    new RuntimeException("Funcionário não encontrado"));

    assistencia.setDataSolicitacao(dto.dataSolicitacao());
    assistencia.setDataAgendamento(dto.dataAgendamento());
    assistencia.setDescricao(dto.descricao());
    assistencia.setDataEntregaOriginal(dto.dataEntregaOriginal());
    assistencia.setStatus(dto.status());
    assistencia.setValorServico(dto.valorServico());
    assistencia.setValorMateriais(dto.valorMateriais());

    assistencia.setCliente(cliente);
    assistencia.setPedido(pedido);
    assistencia.setFuncionario(funcionario);

    verificarGarantia(assistencia);

    AssistenciaTecnica atualizada =
            assistenciaRepository.save(assistencia);

    return converterParaResponse(atualizada);
}


    public void excluir(Long id) {

        AssistenciaTecnica assistencia =
                assistenciaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assistência técnica não encontrada"));

        assistenciaRepository.delete(assistencia);
    }

    public ItemAssistenciaTecnicaResponseDTO adicionarMaterial(
        Long assistenciaId,
        ItemAssistenciaTecnicaRequestDTO dto) {

    AssistenciaTecnica assistencia =
            assistenciaRepository.findById(assistenciaId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Assistência técnica não encontrada"));

    Material material =
            materialRepository.findById(dto.materialId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Material não encontrado"));

   if (dto.quantidade() <= 0) {
    throw new RuntimeException(
            "A quantidade deve ser maior que zero");
}

if (material.getQuantidade() < dto.quantidade()) {
    throw new RuntimeException(
            "Quantidade insuficiente em estoque");
}

ItemAssistenciaTecnica item = new ItemAssistenciaTecnica();

item.setQuantidade(dto.quantidade());
item.setAssistencia(assistencia);
item.setMaterial(material);

ItemAssistenciaTecnica salvo =
        itemAssistenciaRepository.save(item);

material.setQuantidade(
        material.getQuantidade() - dto.quantidade()
);

materialRepository.save(material);

calcularValorMateriais(assistencia);
assistenciaRepository.save(assistencia);

    return new ItemAssistenciaTecnicaResponseDTO(
            salvo.getId(),
            salvo.getQuantidade(),
            material.getId(),
            material.getNome()
        );
        }

       private void calcularValorMateriais(
        AssistenciaTecnica assistencia) {

    List<ItemAssistenciaTecnica> itens =
            itemAssistenciaRepository
                    .findByAssistenciaId(assistencia.getId());

    BigDecimal total = BigDecimal.ZERO;

    for (ItemAssistenciaTecnica item : itens) {

        BigDecimal quantidade =
                BigDecimal.valueOf(item.getQuantidade());

        BigDecimal valor =
                item.getMaterial().getValorUnitario();

        total = total.add(quantidade.multiply(valor));
    }

    assistencia.setValorMateriais(total.doubleValue());

    verificarGarantia(assistencia);
}
 private void verificarGarantia(
        AssistenciaTecnica assistencia) {

    LocalDate limite =
            assistencia.getDataEntregaOriginal().plusYears(2);

    boolean dentro =
            !LocalDate.now().isAfter(limite);

    assistencia.setDentroGarantia(dentro);

    Double materiais = assistencia.getValorMateriais();

    if (materiais == null) {
        materiais = 0.0;
        assistencia.setValorMateriais(materiais);
    }

    if (dentro) {

        assistencia.setValorServico(0.0);

    } else {

        if (assistencia.getValorServico() == null) {
            assistencia.setValorServico(0.0);
        }
    }

    assistencia.setValorTotal(
            assistencia.getValorServico() + materiais
    );
}

    private AssistenciaTecnicaResponseDTO converterParaResponse(
            AssistenciaTecnica assistencia) {

        return new AssistenciaTecnicaResponseDTO(
                assistencia.getId(),
                assistencia.getDataSolicitacao(),
                assistencia.getDataAgendamento(),
                assistencia.getDescricao(),
                assistencia.getDataEntregaOriginal(),
                assistencia.getDentroGarantia(),
                assistencia.getValorServico(),
                assistencia.getValorMateriais(),
                assistencia.getValorTotal(),
                assistencia.getStatus(),
                assistencia.getCliente().getId(),
                assistencia.getPedido().getId(),
                assistencia.getFuncionario().getId()
        );
    }
}