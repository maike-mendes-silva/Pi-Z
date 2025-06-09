package com.progweb.trabalho.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public long contarTotalUsuarios(){
        return usuarioRepository.count();
    }

    public Optional<Usuario> acharPorEmaileSenha(String email, String senha) {
        return usuarioRepository.findByEmailAndSenha(email, senha);
    }



}
