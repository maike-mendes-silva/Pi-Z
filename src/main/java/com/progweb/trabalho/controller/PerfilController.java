package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService UsuarioService;

    // Rota para perfil. Se tiver usuário logado -> perfil. Se não -> login
    @GetMapping
    public String perfil(HttpSession session, Model model) {
        
        if(session.getAttribute("usuarioID") == null)
            return "redirect:/login";

        long usuarioID = (long) session.getAttribute("usuarioID");
        Optional<Usuario> usuario = UsuarioService.acharPorId(usuarioID);
        model.addAttribute("usuario", usuario.get());        
        return "perfil";
    }

}
