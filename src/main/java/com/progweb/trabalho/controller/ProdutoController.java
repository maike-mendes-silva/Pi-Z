package com.progweb.trabalho.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.service.ProdutoService;

@Controller
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @PostMapping("/produtos")
    public String salvarProduto(@Validated @ModelAttribute("novoProduto") Produto produto,
                                BindingResult result,
                                Model model,
                                @RequestParam("imagem") MultipartFile imagem) {
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

        // Lógica para tratamento da imagem, que será adicionada no caminho definido por UPLOAD_DIR, sendo o mesmo salvo no BD
        if (imagem.isEmpty()) {
            model.addAttribute("erro", "Por favor, selecione um arquivo de imagem.");
            return "cadastrarProduto"; // Ou a view do seu formulário
        }

        String imageUrl = null; // Para armazenar o caminho da imagem que você vai salvar no DB

        try {
            // Garante que o diretório de upload exista
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gera um nome de arquivo único
            String originalFileName = imagem.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Caminho completo onde o arquivo será salvo
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);

            // Salva o arquivo no sistema de arquivos local
            Files.copy(imagem.getInputStream(), filePath);

            // A URL relativa que será salva no banco de dados
            imageUrl = "/uploads/" + uniqueFileName;

            // Salva a URL no objeto Produto
            produto.setImgUrl(imageUrl);
        
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao fazer upload da imagem: " + e.getMessage());
            return "cadastrarProduto"; // Retorna para a view do formulário com erro
        } catch (Exception e) { // Captura outras exceções ao salvar no DB, por exemplo
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao cadastrar produto: " + e.getMessage());
            return "cadastrarProduto";
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
        try{
            produtoService.deletar(id);
        } catch (IOException e){
            e.printStackTrace();
        }
        return "redirect:/perfil";
    }

}
