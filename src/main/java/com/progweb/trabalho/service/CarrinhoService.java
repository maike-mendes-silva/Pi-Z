package com.progweb.trabalho.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.model.ItemCarrinho;
import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.repository.CarrinhoRepository;
import com.progweb.trabalho.repository.ItemCarrinhoRepository;
import com.progweb.trabalho.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    public Carrinho acharPorId(long id){
        return carrinhoRepository.findByUsuarioId(id);
    }

    public Carrinho salvar(Carrinho carrinho){
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho obterOuCriarCarrinhoParaUsuario(Usuario usuario) {
        // Busca o carrinho associado a este usuário específico
        Optional<Carrinho> carrinhoOptional = carrinhoRepository.findByUsuario(usuario);

        if (carrinhoOptional.isPresent()) {
            //System.out.println("====== Encontrou um carrinho!! ======");
            return carrinhoOptional.get(); // Retorna o carrinho existente
        } else {
            // Se não encontrou, cria um novo carrinho e o associa ao usuário
            //System.out.println("====== Não encontrou um carrinho!! ======");
            Carrinho novoCarrinho = new Carrinho();
            novoCarrinho.setUsuario(usuario);
            return carrinhoRepository.save(novoCarrinho);
        }
    }

    @Transactional
    public void adicionarItem(String emailUsuario, Produto produto, int quantidade){
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailUsuario);

        Carrinho carrinho = obterOuCriarCarrinhoParaUsuario(usuarioOpt.get());

        Optional<ItemCarrinho> ItensExistentesOpt = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .findFirst();

        if (ItensExistentesOpt.isPresent()) {
            // Se o item já existe, atualiza a quantidade
            ItemCarrinho itemExistente = ItensExistentesOpt.get();
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
            itemCarrinhoRepository.save(itemExistente); // Salva a atualização
        } else {
            // Se o item não existe, cria um novo ItemCarrinho
            ItemCarrinho newItem = new ItemCarrinho();
            newItem.setCarrinho(carrinho); // Associa ao carrinho
            newItem.setProduto(produto);   // Associa ao produto
            newItem.setQuantidade(quantidade);
            // Opcional: Salvar o preço unitário no momento da adição
            newItem.setPrecoUnitarioNoMomentoDaAdicao(produto.getPreco());

            carrinho.getItens().add(newItem); // Adiciona à lista do carrinho
            itemCarrinhoRepository.save(newItem); // Salva o novo item
        }

        carrinhoRepository.save(carrinho); // Salva o carrinho (para garantir que todas as associações e updates sejam persistidos)

    }

    @Transactional
    public void removerItem(String emailUsuario, Long itemId) {
    
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                                         .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Carrinho carrinho = obterOuCriarCarrinhoParaUsuario(usuario);

        ItemCarrinho itemParaRemover = carrinho.getItens().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item com ID " + itemId + " não encontrado no carrinho do usuário."));

        carrinho.getItens().remove(itemParaRemover);
        carrinhoRepository.save(carrinho);
        
    }

    @Transactional
    public void limparCarrinho(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                                         .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Carrinho carrinho = obterOuCriarCarrinhoParaUsuario(usuario);
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);
    }


}
