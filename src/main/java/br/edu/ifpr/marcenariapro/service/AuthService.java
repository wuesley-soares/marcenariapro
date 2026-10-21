package br.edu.ifpr.marcenariapro.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.marcenariapro.dto.LoginRequestDTO;
import br.edu.ifpr.marcenariapro.dto.LoginResponseDTO;
import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;
import br.edu.ifpr.marcenariapro.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new RuntimeException(
                                "E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(
                dto.senha(),
                usuario.getSenha())) {

            throw new RuntimeException(
                    "E-mail ou senha inválidos");
        }

        if (!usuario.getAtivo()) {

            throw new RuntimeException(
                    "Usuário inativo");
        }

        String token = jwtService.gerarToken(usuario);

        return new LoginResponseDTO(
                "Login realizado com sucesso",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                token
        );
    }
}