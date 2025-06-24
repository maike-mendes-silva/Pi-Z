package com.progweb.trabalho.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto acharPorNomeColecaoTamanho (String nome, String colecao, int tamanho){
        return produtoRepository.findByNomeAndColecaoAndTamanho(nome, colecao, tamanho);
    }
    
    public Produto salvar(Produto produto){
        return produtoRepository.save(produto);
    }

    public Optional<Produto> acharPorId(long id){
        return produtoRepository.findById(id);
    }

    public List<Produto> acharTodos(){
        return produtoRepository.findAll();
    }

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Transactional
    public void deletar(long id) throws IOException{

        //Deleta a imagem do produto salva nas pastas do projeto
        Optional<Produto> produtoOptional = produtoRepository.findById(id);

        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            String imgUrlRelativo = produto.getImgUrl(); // Pega o caminho /uploads/nome-unico.jpg
            String fileName = Paths.get(imgUrlRelativo).getFileName().toString();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            produtoRepository.deleteById(id);
            System.out.println("Registro do produto " + id + " deletado do banco de dados.");

            //Deleta o arquivo de imagem do sistema de arquivos
            if (Files.exists(filePath)) { // Verifica se o arquivo realmente existe antes de tentar deletar
                Files.delete(filePath);
                System.out.println("Arquivo de imagem deletado: " + filePath.toString());
            } else {
                System.out.println("Arquivo de imagem não encontrado no caminho: " + filePath.toString() + ". Pode já ter sido deletado ou o caminho está incorreto.");
            }

        }
    }

    public List<Produto> getProdutosAgrupadosPorColecaoENome() {
        List<Produto> produtos = produtoRepository.findAll(); // Busca todos os produtos

        return produtos.stream()
            .collect(Collectors.groupingBy(produto -> new SimpleEntry<>(produto.getColecao(), produto.getNome()), // Chave composta
                Collectors.collectingAndThen(Collectors.toList(), // Coleta os produtos do grupo em uma lista
            list -> list.get(0)))) // Seleciona o primeiro produto da lista
            .values() // Pega os valores do mapa resultante (que são os produtos representativos)
            .stream() // Converte a coleção de produtos representativos em um Stream
            .collect(Collectors.toList()); // Coleta de volta para uma lista
    }

    //Sem agrupar
    public List<Produto> acharPorNomeColecao(String nome, String colecao) {
        return produtoRepository.findByNomeAndColecao(nome, colecao); // Busca todos os produtos
    }

}
