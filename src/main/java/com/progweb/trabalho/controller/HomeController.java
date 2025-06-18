package com.progweb.trabalho.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.service.ProdutoService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Busca os produtos Agrupados por Coleção e Nome
        List<Produto> produtos = produtoService.getProdutosAgrupadosPorColecaoENome();

        // Adiciona a lista no Model para o Thymeleaf
        model.addAttribute("produtos", produtos);

        // Retorna o template home.html
        return "home";
    }
}
