package com.progweb.trabalho.config;

import com.progweb.trabalho.model.Usuario; 
import com.progweb.trabalho.service.UsuarioService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // Indica que esta classe fornece conselhos globais para controladores
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Adiciona o objeto Usuario logado ao Model para todas as views.
     * O atributo no Model será acessível como 'currentUser'.
     * Se o usuário não estiver logado ou não for encontrado, 'currentUser' será null.
     */

    @ModelAttribute("currentUser")
    public Usuario addCurrentUserToModel(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String emailUsuarioLogado = authentication.getName();
            return usuarioService.acharPorEmail(emailUsuarioLogado).orElse(null);
        }
        return null; 
    }
}