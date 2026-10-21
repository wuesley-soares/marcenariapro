package br.edu.ifpr.marcenariapro.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        if (!jwtService.validarToken(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extrairEmail(token);

        Usuario usuario =
                usuarioRepository.findByEmail(email)
                        .orElse(null);

if (usuario != null) {
    System.out.println("Usuário autenticado: " + usuario.getEmail());
    System.out.println("Perfil no banco: " + usuario.getPerfil());
    System.out.println("Ativo: " + usuario.getAtivo());
}

if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo())) {
    filterChain.doFilter(request, response);
    return;
}

SimpleGrantedAuthority autoridade =
        new SimpleGrantedAuthority(
                "ROLE_" + usuario.getPerfil().name()
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        java.util.List.of(autoridade)
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}