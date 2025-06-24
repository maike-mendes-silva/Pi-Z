package com.progweb.trabalho.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.model.Usuario;

@Repository
public interface CarrinhoRepository extends JpaRepository <Carrinho,Long>{

    Carrinho findByUsuarioId(Long usuarioId);

    Optional<Carrinho> findByUsuario(Usuario usuario);

}
