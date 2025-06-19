package com.progweb.trabalho.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Importação necessária

import com.progweb.trabalho.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) { // Spring injeta seu UsuarioService aqui
        return email -> { // A lambda implementa o método loadUserByUsername(String username)
            return usuarioService.acharPorEmail(email) // Busca seu objeto Usuario do banco pelo email
                    .map(usuario -> new org.springframework.security.core.userdetails.User(
                            usuario.getEmail(), // Username (neste caso, o email)
                            usuario.getSenha(), // <<<<< AQUI: A SENHA DEVE VIR JÁ CODIFICADA (HASHEADA) DO BANCO DE DADOS
                            // Coleção de Authorities/Roles do usuário
                            Collections.singletonList(new SimpleGrantedAuthority(usuario.isEhAdmin() ? "ROLE_ADMIN" : "ROLE_USER"))
                    ))
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 1. URLs que exigem autenticação (usuário logado)
                // Proteger a página de perfil e tudo que está abaixo dela (/perfil, /perfil/alterar-senha, etc.)
                .requestMatchers("/perfil/**").authenticated()
                // Proteger a página do carrinho e tudo que está abaixo dela
                .requestMatchers("/carrinho/**").authenticated()

                // Permitir acesso ao console do H2
                // Colocado aqui para que seja avaliado antes do anyRequest().permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()

                // 2. Permitir acesso público a TODAS as outras URLs
                // Isso inclui a página de login, cadastro, home, produtos, etc.,
                // e também recursos estáticos como CSS, JS e imagens.
                .anyRequest().permitAll()
            )
            // 3. Configuração do Formulário de Login
            .formLogin(form -> form
                .loginPage("/login") // Sua página de login personalizada
                .defaultSuccessUrl("/", true) // Redireciona para a página inicial após login
                .permitAll()
            )
            // 4. Configuração de Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        // --- ADICIONE ESTAS DUAS LINHAS CRUCIAIS PARA O H2-CONSOLE ---
        // Desabilita a proteção CSRF especificamente para as URLs do h2-console.
        // Isso é necessário porque o console do H2 faz algumas requisições que podem ser interpretadas como CSRF.
        http.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")));

        // Permite que o h2-console seja exibido em um iframe.
        // Por padrão, o Spring Security bloqueia iframes para proteger contra clickjacking.
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        // --- FIM DAS LINHAS A SEREM ADICIONADAS ---

        return http.build();
    }
}