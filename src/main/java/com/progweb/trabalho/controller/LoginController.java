package com.progweb.trabalho.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.progweb.trabalho.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    //Redireciona para a tela de login
    @GetMapping // Mapeia para GET /login
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) { // Adicione o parâmetro Model

        if (error != null) {
            model.addAttribute("mensagem", "Email ou senha inválidos. Por favor, tente novamente.");
        }
        if (logout != null) {
            model.addAttribute("mensagem", "Você foi desconectado com sucesso.");
        }
        return "login";
    }

    @PostMapping("/sair")
    public String sair(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
