package com.progweb.trabalho.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.progweb.trabalho.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository <Produto,Long>{

    Produto findByNomeAndColecaoAndTamanho(String nome, String colecao, int tamanho);

    List<Produto> findByNomeAndColecao(String nome, String colecao);
}
