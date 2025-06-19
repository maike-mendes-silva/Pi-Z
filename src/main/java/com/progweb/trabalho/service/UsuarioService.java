package com.progweb.trabalho.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar o PasswordEncoder
import org.springframework.stereotype.Service;

import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetar o PasswordEncoder

    /**
     * Salva um novo usuário ou atualiza um existente.
     * Importante: Este método deve codificar a senha antes de salvar um *novo* usuário.
     * Se for um método de atualização, você deve ter cuidado para não recodificar uma senha já codificada
     * ou permitir que a senha seja alterada inadvertidamente.
     * Para um novo usuário, a senha deve ser codificada aqui.
     * Ex: usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
     */
    public Usuario salvar(Usuario usuario) {
        // Exemplo: codificar a senha antes de salvar um NOVO usuário.
        // Se este método é usado tanto para criação quanto para atualização,
        // você precisará de uma lógica para verificar se a senha já está codificada ou se foi alterada.
        if (usuario.getId() == null || usuario.getSenha() != null && !usuario.getSenha().startsWith("$2a$")) { // Verifica se é novo ou senha não codificada
             usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public long contarTotalUsuarios(){
        return usuarioRepository.count();
    }

    /**
     * Autenticação de usuário.
     * Esta busca *não* deve comparar senhas em texto puro.
     * Deve-se buscar o usuário pelo email e depois comparar as senhas codificadas.
     */
    public Optional<Usuario> autenticarUsuario(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email); // Buscar pelo email primeiro

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparar a senha fornecida (texto puro) com a senha codificada no banco de dados
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty(); // Retorna vazio se o email não for encontrado ou a senha não corresponder
    }
    public Optional<Usuario> acharPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> acharPorId(long id){
        return usuarioRepository.findById(id);
    }

    // --- Novo método para alterar a senha ---
    public void alterarSenha(long usuarioId, String senhaAtual, String novaSenha) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOptional.get();

        // 1. Verificar se a senha atual fornecida corresponde à senha codificada no banco
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        // 2. Codificar a nova senha antes de salvar
        String novaSenhaCodificada = passwordEncoder.encode(novaSenha);

        // 3. Atualizar a senha do usuário
        usuario.setSenha(novaSenhaCodificada);
        usuarioRepository.save(usuario); // Salva o usuário com a nova senha codificada
    }


}