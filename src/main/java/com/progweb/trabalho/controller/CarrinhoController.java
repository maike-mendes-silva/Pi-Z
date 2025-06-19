package com.progweb.trabalho.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.CarrinhoService;
import com.progweb.trabalho.service.UsuarioService;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String index(Authentication authentication, Model model) {
        // Obtenha o email do usuário autenticado pelo Spring Security
        String emailUsuarioLogado = authentication.getName(); // authentication.getName() retorna o "username" (seu email)

        Long usuarioID;
        Usuario usuario;
        try {
            // Busque o objeto Usuario completo usando o email
            usuario = usuarioService.acharPorEmail(emailUsuarioLogado)
                                    .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado no sistema."));
            usuarioID = usuario.getId();
        } catch (UsernameNotFoundException e) {
            // Isso não deveria acontecer se o usuário está autenticado, mas é um fallback seguro.
            // Faça logout do usuário se ele não for encontrado no DB.
            return "redirect:/logout";
        }

        Carrinho carrinho = carrinhoService.acharPorId(usuarioID); // Supondo que isso retorna Carrinho ou null

        //Se o usuário não tiver carrinho, cria um novo
        if(carrinho == null){
            carrinho = new Carrinho();
            carrinho.setUsuario(usuario);
            carrinhoService.salvar(carrinho);
            model.addAttribute("carrinho", carrinho);
        } else{
            //Se tiver carrinho, seleciona o que está no banco
            model.addAttribute("carrinho", carrinho);
        }

        return "carrinho";
    }

}
