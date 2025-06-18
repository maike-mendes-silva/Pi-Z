package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.CarrinhoService;
import com.progweb.trabalho.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String index(HttpSession session, Model model) {
        
        //Se não tiver usuário logado, redireciona para login
        if(session.getAttribute("usuarioID") == null){
            return "redirect:/login";
        }

        //Se o usuário não tiver carrinho, cria um novo
        long usuarioID = (long) session.getAttribute("usuarioID");
        Optional<Usuario> usuario = usuarioService.acharPorId(usuarioID);
        if(carrinhoService.acharPorId(usuarioID) == null){
            Carrinho carrinho = new Carrinho();
            carrinho.setUsuario(usuario.get());
            carrinhoService.salvar(carrinho);
            model.addAttribute("carrinho", carrinho);
        } else{
            //Se tiver carrinho, seleciona o que está no banco
            Carrinho carrinho = carrinhoService.acharPorId(usuarioID);
            model.addAttribute("carrinho", carrinho);
        }


        return "carrinho";
    }

}
