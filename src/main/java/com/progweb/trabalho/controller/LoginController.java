package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.repository.UsuarioRepository;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping
    public String login(){
        return "login";
    }

    @PostMapping("/entrar")
    public String entrar(@RequestParam String email, @RequestParam String senha){

        Optional<Usuario> usuario = usuarioRepository.findByEmailAndSenha(email, senha);
        if (usuario.isPresent()) {
            return "redirect:/";   
        } else {
            return "redirect:/login";
        }
    }

}
