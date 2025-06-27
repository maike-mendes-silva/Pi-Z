package com.progweb.trabalho.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.model.ItemCarrinho;
import com.progweb.trabalho.model.Produto;
import com.progweb.trabalho.model.Usuario;
import com.progweb.trabalho.repository.CarrinhoRepository;
import com.progweb.trabalho.repository.ItemCarrinhoRepository;
import com.progweb.trabalho.repository.ProdutoRepository;
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

    @Autowired
    private ProdutoRepository produtoRepository;

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
    public void finalizarCompra(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                                         .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Carrinho carrinho = obterOuCriarCarrinhoParaUsuario(usuario);

        // Decrementar a quantidade do produto em estoque para cada item do carrinho**
        if (carrinho != null && carrinho.getItens() != null) {
            // Cria uma cópia da coleção para evitar ConcurrentModificationException
            List<ItemCarrinho> itensDoCarrinho = carrinho.getItens();
            
            for (ItemCarrinho item : itensDoCarrinho) {
                Produto produto = item.getProduto(); 
                int quantidadeNoCarrinho = item.getQuantidade();

                if (produto != null) {
                    //Busca o produto novamente do banco para garantir que você tem a versão mais recente
                    Optional<Produto> produtoOpt = produtoRepository.findById(produto.getId());
                    if (produtoOpt.isPresent()) {
                        Produto produtoEmEstoque = produtoOpt.get();
                        
                        // Verifica se há estoque suficiente antes de decrementar
                        if (produtoEmEstoque.getQuantidade() >= quantidadeNoCarrinho) {
                            produtoEmEstoque.setQuantidade(produtoEmEstoque.getQuantidade() - quantidadeNoCarrinho);
                            produtoRepository.save(produtoEmEstoque);
                            //System.out.println("Produto " + produtoEmEstoque.getNome() + " quantidade atualizada para: " + produtoEmEstoque.getQtd());
                        } else {
                            System.err.println("Estoque insuficiente para o produto: " + produtoEmEstoque.getNome() + ". Disponível: " + produtoEmEstoque.getQuantidade() + ", Necessário: " + quantidadeNoCarrinho);
                        }
                    } else {
                        System.err.println("Produto com ID " + produto.getId() + " não encontrado no banco de dados.");
                    }
                }
            }
        }

        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);
    }


}
