package com.progweb.trabalho.controller;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Busca todos os produtos no banco
        List<Produto> produtos = produtoRepository.findAll();

        // Adiciona a lista no Model para o Thymeleaf
        model.addAttribute("produtos", produtos);

        // Retorna o template home.html
        return "home";
    }
}
