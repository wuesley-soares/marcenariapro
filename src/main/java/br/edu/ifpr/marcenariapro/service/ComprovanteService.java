
package br.edu.ifpr.marcenariapro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComprovanteService {

    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final Path diretorio;

    public ComprovanteService(
            @Value("${app.upload.dir:./comprovantes}") String caminho) {

        this.diretorio = Paths.get(caminho)
                .toAbsolutePath()
                .normalize();
    }

    public String salvar(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O comprovante não pode ultrapassar 5 MB."
            );
        }

        String tipo = arquivo.getContentType();

        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato inválido. Envie PDF, JPG ou PNG."
            );
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = obterExtensao(nomeOriginal);
        String nomeSeguro = UUID.randomUUID() + extensao;

        try {
            Files.createDirectories(diretorio);

            Path destino = diretorio.resolve(nomeSeguro).normalize();

            if (!destino.startsWith(diretorio)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nome de arquivo inválido."
                );
            }

            Files.copy(arquivo.getInputStream(), destino);

            return destino.toString();

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível salvar o comprovante."
            );
        }
    }

    public Resource carregar(String caminho) {

        try {
            Path arquivo = Paths.get(caminho)
                    .toAbsolutePath()
                    .normalize();

            if (!arquivo.startsWith(diretorio)
                    || !Files.isRegularFile(arquivo)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comprovante não encontrado."
                );
            }

            return new UrlResource(arquivo.toUri());

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Não foi possível localizar o comprovante."
            );
        }
    }

    private String obterExtensao(String nome) {

        if (nome == null || !nome.contains(".")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O arquivo precisa ter uma extensão válida."
            );
        }

        String extensao = nome.substring(nome.lastIndexOf("."))
                .toLowerCase();

        if (!Set.of(".pdf", ".jpg", ".jpeg", ".png").contains(extensao)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Extensão de arquivo não permitida."
            );
        }

        return extensao;
    }
}
