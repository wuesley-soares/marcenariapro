package br.edu.ifpr.marcenariapro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpr.marcenariapro.dto.ImagemAssistenciaTecnicaResponseDTO;
import br.edu.ifpr.marcenariapro.entity.AssistenciaTecnica;
import br.edu.ifpr.marcenariapro.entity.ImagemAssistenciaTecnica;
import br.edu.ifpr.marcenariapro.enums.EtapaImagemAssistencia;
import br.edu.ifpr.marcenariapro.repository.AssistenciaTecnicaRepository;
import br.edu.ifpr.marcenariapro.repository.ImagemAssistenciaTecnicaRepository;


@Service
public class ImagemAssistenciaTecnicaService {

    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    private final ImagemAssistenciaTecnicaRepository imagemRepository;
    private final AssistenciaTecnicaRepository assistenciaRepository;
    private final Path diretorioBase;

    public ImagemAssistenciaTecnicaService(
            ImagemAssistenciaTecnicaRepository imagemRepository,
            AssistenciaTecnicaRepository assistenciaRepository,
            @Value("${app.upload.dir:uploads}") String diretorioUpload) {

        this.imagemRepository = imagemRepository;
        this.assistenciaRepository = assistenciaRepository;

        this.diretorioBase = Paths.get(diretorioUpload)
                .toAbsolutePath()
                .normalize()
                .resolve("assistencias");
    }

   
@Transactional
public ImagemAssistenciaTecnicaResponseDTO salvar(
        Long assistenciaId,
        EtapaImagemAssistencia etapa,
        MultipartFile arquivo) {

    if (etapa == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Informe a etapa da imagem."
        );
    }

    if (arquivo == null || arquivo.isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Selecione uma imagem."
        );
    }

    if (arquivo.getSize() > TAMANHO_MAXIMO) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A imagem não pode ultrapassar 5 MB."
        );
    }

    String tipo = arquivo.getContentType();

    if (!"image/jpeg".equalsIgnoreCase(tipo)
            && !"image/png".equalsIgnoreCase(tipo)) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "São permitidas somente imagens JPG, JPEG e PNG."
        );
    }

    validarImagemReal(arquivo);

    AssistenciaTecnica assistencia =
            assistenciaRepository.findById(assistenciaId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Assistência técnica não encontrada."
                            ));

    if (etapa == EtapaImagemAssistencia.APOS_ASSISTENCIA
            && assistencia.getStatus()
                    != br.edu.ifpr.marcenariapro.enums.StatusAssistencia.CONCLUIDA) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "As fotos após a assistência só podem ser adicionadas quando o atendimento estiver concluído."
        );
    }

    if (etapa == EtapaImagemAssistencia.PROBLEMA
            && assistencia.getStatus()
                    != br.edu.ifpr.marcenariapro.enums.StatusAssistencia.ABERTA
            && assistencia.getStatus()
                    != br.edu.ifpr.marcenariapro.enums.StatusAssistencia.AGENDADA) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "As fotos do problema só podem ser adicionadas antes do início do atendimento."
        );
    }

    String extensao = "image/png".equalsIgnoreCase(tipo)
            ? ".png"
            : ".jpg";

    String nomeArquivo = UUID.randomUUID() + extensao;

    Path pastaAssistencia = diretorioBase
            .resolve(String.valueOf(assistenciaId))
            .normalize();

    Path destino = pastaAssistencia.resolve(nomeArquivo).normalize();

    if (!destino.startsWith(pastaAssistencia)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Caminho de arquivo inválido."
        );
    }

    try {
        Files.createDirectories(pastaAssistencia);
        arquivo.transferTo(destino);

        ImagemAssistenciaTecnica imagem =
                new ImagemAssistenciaTecnica();

        imagem.setNomeOriginal(arquivo.getOriginalFilename() == null
                ? nomeArquivo
                : arquivo.getOriginalFilename());

        imagem.setNomeArquivo(nomeArquivo);
        imagem.setCaminhoArquivo(destino.toString());
        imagem.setTipoConteudo(tipo);
        imagem.setEtapa(etapa);
        imagem.setAssistencia(assistencia);

        return converterParaResponse(
                imagemRepository.save(imagem)
        );

    } catch (IOException e) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível salvar a imagem."
        );
    }
}


    @Transactional(readOnly = true)
    public List<ImagemAssistenciaTecnicaResponseDTO> listar(
            Long assistenciaId,
            EtapaImagemAssistencia etapa) {

        if (!assistenciaRepository.existsById(assistenciaId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Assistência técnica não encontrada."
            );
        }

        List<ImagemAssistenciaTecnica> imagens = etapa == null
                ? imagemRepository
                        .findByAssistenciaIdOrderByDataUploadDesc(assistenciaId)
                : imagemRepository
                        .findByAssistenciaIdAndEtapaOrderByDataUploadDesc(
                                assistenciaId,
                                etapa
                        );

        return imagens.stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource buscarArquivo(
            Long assistenciaId,
            Long imagemId) {

        ImagemAssistenciaTecnica imagem = buscarImagem(
                assistenciaId,
                imagemId
        );

        Path caminho = Paths.get(imagem.getCaminhoArquivo())
                .toAbsolutePath()
                .normalize();

        Resource recurso = new FileSystemResource(caminho);

        if (!recurso.exists() || !recurso.isReadable()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Arquivo da imagem não encontrado."
            );
        }

        return recurso;
    }

    @Transactional
public void excluir(Long assistenciaId, Long imagemId) {

    ImagemAssistenciaTecnica imagem = buscarImagem(
            assistenciaId,
            imagemId
    );

    try {
        Files.deleteIfExists(
                Paths.get(imagem.getCaminhoArquivo())
        );

        imagemRepository.delete(imagem);

    } catch (IOException e) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível excluir a imagem."
        );
    }
}

    private ImagemAssistenciaTecnica buscarImagem(
            Long assistenciaId,
            Long imagemId) {

        return imagemRepository.findById(imagemId)
                .filter(imagem ->
                        imagem.getAssistencia().getId().equals(assistenciaId))
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Imagem não encontrada nesta assistência."
                        ));
    }

    private void validarImagemReal(MultipartFile arquivo) {

        try (var entrada = arquivo.getInputStream()) {

            if (ImageIO.read(entrada) == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O arquivo enviado não é uma imagem válida."
                );
            }

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não foi possível validar a imagem."
            );
        }
    }

    private ImagemAssistenciaTecnicaResponseDTO converterParaResponse(
            ImagemAssistenciaTecnica imagem) {

        return new ImagemAssistenciaTecnicaResponseDTO(
                imagem.getId(),
                imagem.getNomeOriginal(),
                imagem.getTipoConteudo(),
                imagem.getEtapa(),
                imagem.getDataUpload()
        );
    }

    @Transactional
public void excluirTodas(Long assistenciaId) {

    List<ImagemAssistenciaTecnica> imagens =
            imagemRepository
                    .findByAssistenciaIdOrderByDataUploadDesc(assistenciaId);

    for (ImagemAssistenciaTecnica imagem : imagens) {
        try {
            Files.deleteIfExists(
                    Paths.get(imagem.getCaminhoArquivo())
            );
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível excluir todas as imagens da assistência."
            );
        }

        imagemRepository.delete(imagem);
    }
}

}