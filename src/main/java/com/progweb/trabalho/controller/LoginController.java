package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    //Redireciona para a tela de login
    @GetMapping
    public String login(){
        return "login";
    }

    //Verifica se as credenciais estão certas e loga
    @PostMapping("/entrar")
    public String entrar(@RequestParam String email, @RequestParam String senha, RedirectAttributes attributes, HttpSession session){

        Optional<Usuario> usuario = usuarioService.acharPorEmaileSenha(email, senha);
        if (usuario.isPresent()) {
            session.setAttribute("usuarioID", usuario.get().getId());
            return "redirect:/";   
        } else {
            attributes.addFlashAttribute("mensagem", "Email ou senha inválidos!");
            return "redirect:/login";
        }
    }

    @PostMapping("/sair")
    public String sair(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
