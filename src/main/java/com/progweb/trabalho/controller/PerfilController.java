package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.progweb.trabalho.model.Produto; 
import com.progweb.trabalho.model.Usuario;

import com.progweb.trabalho.service.ProdutoService; 
import com.progweb.trabalho.service.UsuarioService;


@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProdutoService produtoService; 

    @GetMapping
    public String perfil(Authentication authentication, Model model) { 
        String emailUsuario = authentication.getName(); 
        Optional<Usuario> usuarioOpt = usuarioService.acharPorEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
           
            return "redirect:/login?error=usuarioNaoEncontrado";
        }

        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("produto", new Produto()); 
        model.addAttribute("produtos", produtoService.acharTodos()); 

        return "perfil"; 
    }

    // FUNCIONALIDADE DE ALTERAÇÃO DE SENHA:

    @PostMapping("/alterar-senha") 
    public String alterarSenha(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 Authentication authentication,
                                 Model model) {

        String email = authentication.getName();
        Long usuarioId = null;

        try {
            usuarioId = usuarioService.acharPorEmail(email)
                                      .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado no sistema."))
                                      .getId();
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage()); 
            return "perfil"; 
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("errorMessage", "A nova senha e a confirmação não correspondem.");
            return "perfil";
        }
        if (newPassword.length() < 8) {
            model.addAttribute("errorMessage", "A nova senha deve ter no mínimo 8 caracteres.");
            return "perfil";
        }

        try {
            usuarioService.alterarSenha(usuarioId, currentPassword, newPassword);
            model.addAttribute("successMessage", "Senha alterada com sucesso!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ocorreu um erro inesperado ao alterar a senha. Tente novamente.");
            e.printStackTrace();
        }
        
        try {
            Usuario usuario = usuarioService.acharPorEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
            model.addAttribute("usuario", usuario);
        } catch (UsernameNotFoundException e) {
            return "redirect:/logout"; 
        }
        model.addAttribute("produtos", produtoService.acharTodos()); 

        return "perfil"; 
    }
}