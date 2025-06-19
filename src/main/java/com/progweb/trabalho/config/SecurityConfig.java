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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; 

import com.progweb.trabalho.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) { 
        return email -> { 
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
                .requestMatchers("/perfil/**").authenticated()
                .requestMatchers("/carrinho/**").authenticated()

                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()

        
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login") // Sua página de login personalizada
                .defaultSuccessUrl("/", true) // Redireciona para a página inicial após login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        
        http.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")));

        
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}