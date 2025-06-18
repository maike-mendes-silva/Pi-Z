package com.progweb.trabalho.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


import com.progweb.trabalho.model.Carrinho;

@Repository
public interface CarrinhoRepository extends JpaRepository <Carrinho,Long>{

    Carrinho findByUsuarioId(Long usuarioId);

}
