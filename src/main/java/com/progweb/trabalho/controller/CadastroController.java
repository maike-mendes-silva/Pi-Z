package com.progweb.trabalho.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.UsuarioService;


@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @Autowired
    private UsuarioService usuarioService;

    //Redireciona para a tela cadastro
    @GetMapping
    public String novoCadastro(Model model){
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "cadastro";
    }

    //Após clicar no botão de cadastro, salva o usuário no banco
    @PostMapping("/salvar")
    public String salvarCadastro(@ModelAttribute Usuario usuario, RedirectAttributes attributes){

        //O primeiro usuário criado é ADMIN
        long totalUsuarios = usuarioService.contarTotalUsuarios();
        if(totalUsuarios == 0){
            usuario.setEhAdmin(true);
        } else{
            usuario.setEhAdmin(false);
        }
        
        this.usuarioService.salvar(usuario);
        attributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
        return "redirect:/login";
    }

}
