package com.progweb.trabalho.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.service.CarrinhoService;
import com.progweb.trabalho.service.ProdutoService;
import com.progweb.trabalho.service.UsuarioService;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String index(Authentication authentication, Model model) {
        // Obtenha o email do usuário autenticado pelo Spring Security
        String emailUsuarioLogado = authentication.getName(); // authentication.getName() retorna o "username" (seu email)

        Long usuarioID;
        Usuario usuario;
        try {
            // Busque o objeto Usuario completo usando o email
            usuario = usuarioService.acharPorEmail(emailUsuarioLogado)
                                    .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado no sistema."));
            usuarioID = usuario.getId();
        } catch (UsernameNotFoundException e) {
            // Isso não deveria acontecer se o usuário está autenticado, mas é um fallback seguro.
            // Faça logout do usuário se ele não for encontrado no DB.
            return "redirect:/logout";
        }

        Carrinho carrinho = carrinhoService.acharPorId(usuarioID); // Supondo que isso retorna Carrinho ou null

        //Se o usuário não tiver carrinho, cria um novo
        if(carrinho == null){
            carrinho = new Carrinho();
            carrinho.setUsuario(usuario);
            carrinhoService.salvar(carrinho);
            model.addAttribute("carrinho", carrinho);
        } else{
            //Se tiver carrinho, seleciona o que está no banco
            model.addAttribute("carrinho", carrinho);
        }

        return "carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionarAoCarrinho(
            Authentication authentication,
            @RequestParam("idProdutoEscolhido") Long idProdutoEscolhido,
            RedirectAttributes redirectAttributes) {

        Optional<Produto> produtoParaCarrinho = produtoService.acharPorId(idProdutoEscolhido);

        if(produtoParaCarrinho.isEmpty()){
            redirectAttributes.addFlashAttribute("mensagemErro", "Produto não encontrado.");
            return "redirect:/home";
        }

        String emailUsuario = authentication.getName();
        carrinhoService.adicionarItem(emailUsuario, produtoParaCarrinho.get(), 1);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto adicionado ao carrinho!");

        // 4. Redirecionar para a página do carrinho ou para a mesma página de detalhes
        return "redirect:/carrinho";
    }

    @PostMapping("/remover")
    public String removerItemDoCarrinho(
            @RequestParam("itemId") Long itemId,
            Authentication authentication,   
            RedirectAttributes redirectAttributes) { 

        String emailUsuario = authentication.getName();

        try {
            carrinhoService.removerItem(emailUsuario, itemId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Item removido do carrinho com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao remover item: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao remover o item.");
        }

        return "redirect:/carrinho";
    }

    @PostMapping("/finalizarCompra")
    public String finalizarCompra(Authentication authentication, RedirectAttributes redirectAttributes) {
        String emailUsuario = authentication.getName();
        carrinhoService.finalizarCompra(emailUsuario);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Pedido criado com sucesso!");
        return "redirect:/";
    }

}
