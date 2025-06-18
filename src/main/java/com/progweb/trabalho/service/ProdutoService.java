package com.progweb.trabalho.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void deletar(long id){
        produtoRepository.deleteById(id);
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

}
