package com.progweb.trabalho.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.progweb.trabalho.model.Usuario;

public interface UsuarioRepository extends CrudRepository <Usuario, Long>{

    Optional<Usuario> findByEmailAndSenha(String email, String senha);

}
