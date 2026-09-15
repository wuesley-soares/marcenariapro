package br.edu.ifpr.marcenariapro.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.LoginRequestDTO;
import br.edu.ifpr.marcenariapro.dto.LoginResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new RuntimeException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RuntimeException("E-mail ou senha inválidos");
        }

        return new LoginResponseDTO(
                "Login realizado com sucesso",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name()
        );
    }
}