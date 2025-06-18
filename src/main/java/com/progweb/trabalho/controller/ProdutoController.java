package com.progweb.trabalho.controller;

import java.lang.reflect.Field;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.service.ProdutoService;

@Controller
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/produtos")
    public String salvarProduto(@Validated @ModelAttribute("novoProduto") Produto produto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erro", "Erro ao cadastrar produto: verifique os dados.");
            carregarProdutos(model);
            return "perfil";
        }

        Produto existente = produtoService.acharPorNomeColecaoTamanho(produto.getNome(), produto.getColecao(), produto.getTamanho());

        if (existente != null) {
            model.addAttribute("produtoExistente", existente);
            model.addAttribute("novoProduto", produto);
            model.addAttribute("mostrarConfirmacao", true);
            carregarProdutos(model);
            return "perfil";
        }

        produtoService.salvar(produto);
        return "redirect:/perfil";
    }

    @PostMapping("/produtos/adicionarQuantidade")
    public String adicionarQuantidade(@RequestParam("id") Long id,
                                      @RequestParam("quantidadeAdicionar") int quantidadeAdicionar) {
        Produto produto = produtoService.acharPorId(id).orElse(null);
        if (produto != null) {
            produto.setQuantidade(produto.getQuantidade() + quantidadeAdicionar);
            produtoService.salvar(produto);
        }
        return "redirect:/perfil";
    }

    private void carregarProdutos(Model model) {
        List<Produto> produtos = produtoService.acharTodos();

        List<Map<String, Object>> listaMapeada = new ArrayList<>();
        List<String> colunas = new ArrayList<>();

        for (Produto produto : produtos) {
            Map<String, Object> mapa = new LinkedHashMap<>();
            for (Field field : Produto.class.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    mapa.put(field.getName(), field.get(produto));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            listaMapeada.add(mapa);
        }

        if (!listaMapeada.isEmpty()) {
            colunas.addAll(listaMapeada.get(0).keySet());
        }

        model.addAttribute("colunas", colunas);
        model.addAttribute("produtos", listaMapeada);
    }
    @PostMapping("/produtos/remover")
    public String removerProduto(@RequestParam("id") Long id) {
        produtoService.deletar(id);
        return "redirect:/perfil";
    }

}
