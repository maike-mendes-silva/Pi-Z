package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.progweb.trabalho.model.Produto; // Certifique-se de que esta importação é usada
import com.progweb.trabalho.model.Usuario;

import com.progweb.trabalho.service.ProdutoService; // Certifique-se de que este serviço é usado
import com.progweb.trabalho.service.UsuarioService;

// Remova ou adapte o uso de HttpSession se você vai confiar no Spring Security para autenticação
// import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/perfil") // Todas as rotas neste controller começam com /perfil
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProdutoService produtoService; // Se você realmente usa ProdutoService aqui

    // Rota para perfil.
    // Confie no Spring Security para garantir que o usuário está autenticado
    // antes de acessar esta rota (configurado em SecurityConfig).
    @GetMapping
    public String perfil(Authentication authentication, Model model) { // Use Authentication

        String emailUsuario = authentication.getName(); // Obtém o email/username do usuário logado
        Optional<Usuario> usuarioOpt = usuarioService.acharPorEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
            // Isso não deveria acontecer se o usuário está autenticado, mas é um fallback
            return "redirect:/login?error=usuarioNaoEncontrado";
        }

        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        // Exemplo de como você usaria Produto:
        model.addAttribute("produto", new Produto()); // para o formulário
        model.addAttribute("produtos", produtoService.acharTodos()); // Para listar produtos, por exemplo

        return "perfil"; // Nome da sua view de perfil (ex: perfil.html)
    }

    // FUNCIONALIDADE DE ALTERAÇÃO DE SENHA:

    // Rota para mostrar o formulário de alteração de senha
    // URL: /perfil/alterar-senha
    @PostMapping("/alterar-senha") // URL para onde o formulário do fragmento enviará os dados
    public String alterarSenha(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 Authentication authentication,
                                 Model model) {

        String email = authentication.getName();
        Long usuarioId = null;

        try {
            usuarioId = usuarioService.acharPorEmail(email)
                                      .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado no sistema."))
                                      .getId();
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage()); // Use errorMessage para consistência
            return "perfil"; // Retorna para a página de perfil
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("errorMessage", "A nova senha e a confirmação não correspondem.");
            return "perfil";
        }
        if (newPassword.length() < 8) {
            model.addAttribute("errorMessage", "A nova senha deve ter no mínimo 8 caracteres.");
            return "perfil";
        }

        try {
            usuarioService.alterarSenha(usuarioId, currentPassword, newPassword);
            model.addAttribute("successMessage", "Senha alterada com sucesso!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ocorreu um erro inesperado ao alterar a senha. Tente novamente.");
            e.printStackTrace();
        }
        
        // **IMPORTANTE**: Após a alteração, recarregue os dados do usuário e produtos para a página de perfil
        // porque ela será renderizada novamente.
        try {
            Usuario usuario = usuarioService.acharPorEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
            model.addAttribute("usuario", usuario);
        } catch (UsernameNotFoundException e) {
            return "redirect:/logout"; // Se o usuário não for mais encontrado, faça logout
        }
        model.addAttribute("produtos", produtoService.acharTodos()); // Exemplo

        return "perfil"; // Retorna para a página de perfil para exibir as mensagens
    }
}