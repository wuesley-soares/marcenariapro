package br.edu.ifpr.marcenariapro.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.UsuarioRequestDTO;
import br.edu.ifpr.marcenariapro.dto.UsuarioResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;

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

    Usuario usuario = new Usuario();

    usuario.setNome(dto.nome());
    usuario.setEmail(dto.email());

    // Criptografa a senha antes de salvar
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
}