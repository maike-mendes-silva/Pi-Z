package com.progweb.trabalho.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    

    @PostMapping("/sair")
    public String sair(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
