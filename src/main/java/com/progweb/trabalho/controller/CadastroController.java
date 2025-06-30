package com.progweb.trabalho.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.UsuarioService;

import jakarta.validation.Valid;


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
    @PostMapping("/salvar") // Ou @PostMapping("/cadastro/salvar") dependendo do @RequestMapping da classe
    public String salvarCadastro(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,                 
                                 @RequestParam("repetirSenha") String repetirSenha,
                                 RedirectAttributes attributes,
                                 Model model){
        //Verificar erros de validação do @Valid primeiro
        if (result.hasErrors()) {
            model.addAttribute("mensagemErro", "Verifique os campos do formulário.");
            return "cadastro";
        }
        // Validação de senhas 
        if(!usuario.getSenha().equals(repetirSenha)){
            model.addAttribute("mensagemErro", "As senhas não coincidem.");
            model.addAttribute("usuario", usuario); // Mantém os dados preenchidos no formulário
            return "cadastro";
        }
        // Verificação de e-mail duplicado (crucial!)
        if (usuarioService.acharPorEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("mensagemErro", "Este e-mail já está cadastrado. Por favor, use outro.");
            model.addAttribute("usuario", usuario); // Mantém os dados preenchidos no formulário
            return "cadastro";
        }
        // Lógica de ADMIN (permanece como está)
        long totalUsuarios = usuarioService.contarTotalUsuarios();
        if(totalUsuarios == 0){
            usuario.setEhAdmin(true);
        } else {
            usuario.setEhAdmin(false);
        }
        
        // 8. Salvar o usuário
        this.usuarioService.salvar(usuario);
        attributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
        return "redirect:/login"; // Redireciona para a página de login
    }

}
