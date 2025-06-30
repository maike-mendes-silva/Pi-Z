package com.progweb.trabalho.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.ProdutoService;
import com.progweb.trabalho.service.UsuarioService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProdutoService produtoService; 

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping
    public String perfil(Authentication authentication, Model model) { 
        String emailUsuario = authentication.getName(); 
        Optional<Usuario> usuarioOpt = usuarioService.acharPorEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
           
            return "redirect:/login?error=usuarioNaoEncontrado";
        }

        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("produto", new Produto()); 
        model.addAttribute("produtos", produtoService.acharTodos()); 

        return "perfil"; 
    }

    // FUNCIONALIDADE DE ALTERAÇÃO DE SENHA:

    @PostMapping("/alterar-senha") 
    public String alterarSenha(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 Authentication authentication,
                                 Model model) {

        String email = authentication.getName();
        Usuario usuario = null;

        try {
            // Busca o usuário completo no início para que ele esteja disponível para o modelo em caso de erro
            usuario = usuarioService.acharPorEmail(email)
                                    .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado no sistema."));
            model.addAttribute("usuario", usuario);
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/logout";
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
            // Passa o ID do usuário do objeto criado
            usuarioService.alterarSenha(usuario.getId(), currentPassword, newPassword);
            model.addAttribute("successMessage", "Senha alterada com sucesso!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ocorreu um erro inesperado ao alterar a senha. Tente novamente.");
            e.printStackTrace();
        }
        
        model.addAttribute("produtos", produtoService.acharTodos()); 

        return "perfil"; 
    }

    @Transactional
    @PostMapping("/atualizarFoto")
    public String atualizarFoto(Model model,
                                @RequestParam("imagem") MultipartFile imagem,
                                Authentication authentication) {
        
        String usuarioEmail = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioService.acharPorEmail(usuarioEmail);
        if(usuarioOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Usuário não logado");
            return "login";
        }
        Usuario usuario = usuarioOpt.get();

        // Lógica para tratamento da imagem, que será adicionada no caminho definido por UPLOAD_DIR, sendo o mesmo salvo no BD
        if (imagem.isEmpty()) {
            model.addAttribute("erro", "Por favor, selecione um arquivo de imagem.");
            return "perfil"; // Ou a view do seu formulário
        }

        String imageUrl = null; // Para armazenar o caminho da imagem que você vai salvar no DB

        String imgUrlRelativoAntigo = usuario.getImgUrl(); // Pega o caminho /uploads/nome-unico.jpg
        if (imgUrlRelativoAntigo != null && !imgUrlRelativoAntigo.isEmpty()) {
            // Extrai apenas o nome do arquivo do caminho relativo salvo no DB
            String fileNameAntigo = Paths.get(imgUrlRelativoAntigo).getFileName().toString();
            // Constrói o caminho absoluto para o arquivo antigo
            Path filePathAntigo = Paths.get(UPLOAD_DIR + fileNameAntigo);

            try {
                // Deleta o arquivo de imagem do sistema de arquivos
                if (Files.exists(filePathAntigo)) {
                    Files.delete(filePathAntigo);
                    System.out.println("Arquivo de imagem antigo deletado: " + filePathAntigo.toString());
                } else {
                    System.out.println("Arquivo de imagem antigo não encontrado no caminho: " + filePathAntigo.toString() + ". Pode já ter sido deletado ou o caminho está incorreto.");
                }
            } catch (IOException e) {
                System.err.println("Erro ao tentar deletar arquivo de imagem antigo: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("erro", "Erro ao deletar imagem antiga: " + e.getMessage());
                return "perfil";
            }
        }

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
            usuario.setImgUrl(imageUrl);
        
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao fazer upload da imagem: " + e.getMessage());
            return "perfil"; // Retorna para a view do formulário com erro
        } catch (Exception e) { // Captura outras exceções ao salvar no DB, por exemplo
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao cadastrar produto: " + e.getMessage());
            return "perfil";
        }

        usuarioService.salvar(usuario);
        return "redirect:/perfil";
    }



}