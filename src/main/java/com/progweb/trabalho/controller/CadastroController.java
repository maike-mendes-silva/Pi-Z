package com.progweb.trabalho.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.repository.UsuarioRepository;


@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String novoCadastro(Model model){
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvarCadastro(@ModelAttribute Usuario usuario){

        //O primeiro usuário criado é ADMIN
        long totalUsuarios = usuarioRepository.count();
        if(totalUsuarios == 0){
            usuario.setEhAdmin(true);
        } else{
            usuario.setEhAdmin(false);
        }
        
        this.usuarioRepository.save(usuario);
        System.out.println("Usuário salvo com sucesso!");
        return "redirect:/login";
    }

}
