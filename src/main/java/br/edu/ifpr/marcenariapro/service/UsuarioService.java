
package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpr.marcenariapro.dto.UsuarioRequestDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioResponseDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioAtualizacaoDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioSenhaDTO;
import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {

        // Verifica se o e-mail já está cadastrado
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setPerfil(dto.perfil());

        Usuario usuarioSalvo = repository.save(usuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getPerfil(),
                usuarioSalvo.getAtivo()
        );
    }

    public List<UsuarioResponseDTO> listar() {

        return repository.findAll()
                .stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getPerfil(),
                        usuario.getAtivo()
                ))
                .toList();
    }

    public UsuarioResponseDTO atualizar(
            Long id,
            UsuarioAtualizacaoDTO dto) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Funcionário não encontrado."
                        ));

        repository.findByEmail(dto.email())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Este e-mail já está cadastrado."
                    );
                });

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setPerfil(dto.perfil());
        usuario.setAtivo(dto.ativo());

        Usuario atualizado = repository.save(usuario);

        return new UsuarioResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getPerfil(),
                atualizado.getAtivo()
        );
    }

    public void alterarSenha(Long id, UsuarioSenhaDTO dto) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Funcionário não encontrado."
                        ));

        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        repository.save(usuario);
    }
}
