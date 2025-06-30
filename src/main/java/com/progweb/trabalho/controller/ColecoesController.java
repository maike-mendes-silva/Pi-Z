package com.progweb.trabalho.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.service.ProdutoService;

@Controller
@RequestMapping("/colecoes")
public class ColecoesController {

    @Autowired
    private ProdutoService produtoService;
    
    @RequestMapping
    public String exibirColecoesAgrupadas(Model model) {
        // Obtém o mapa de produtos agrupados por coleção do serviço
        Map<String, List<Produto>> produtosPorColecao = produtoService.getProdutosAgrupadosPorColecao();
        
        // Adiciona o mapa ao modelo. Este nome ("produtosPorColecao") é crucial para o HTML/JS.
        model.addAttribute("produtosPorColecao", produtosPorColecao);

        // Retorna o nome do template da sua página de coleções
        return "colecoes";
    }
    
}
