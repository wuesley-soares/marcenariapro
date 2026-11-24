
package br.edu.ifpr.marcenariapro.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ifpr.marcenariapro.entity.Usuario;
import br.edu.ifpr.marcenariapro.enums.PerfilUsuario;
import br.edu.ifpr.marcenariapro.repository.UsuarioRepository;

@Configuration
@Profile("local")
public class AdminInicializador {

    @Bean
    CommandLineRunner criarUsuariosDemonstracao(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            criarUsuario(
                repository,
                passwordEncoder,
                "Administrador",
                "admin@marcenariapro.com",
                "Admin123",
                PerfilUsuario.ADMIN
            );

            criarUsuario(
                repository,
                passwordEncoder,
                "Vendedor Demonstração",
                "vendedor@marcenariapro.com",
                "Vendedor123",
                PerfilUsuario.VENDEDOR
            );

            criarUsuario(
                repository,
                passwordEncoder,
                "Marceneiro Demonstração",
                "marceneiro@marcenariapro.com",
                "Marceneiro123",
                PerfilUsuario.MARCENEIRO
            );

        };
    }

    private void criarUsuario(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            String nome,
            String email,
            String senha,
            PerfilUsuario perfil) {

        if (repository.findByEmail(email).isPresent()) {
            System.out.println(
                "Usuário já cadastrado: " + email
            );
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        repository.save(usuario);

        System.out.println(
            "Usuário de demonstração criado: " + email
        );
    }
}
