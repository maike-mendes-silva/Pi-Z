package com.progweb.trabalho.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Carrinho {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true) // UNIQUE para garantir 1:1
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>(); 

    public double calcularTotal() {
        if (this.itens == null || this.itens.isEmpty()) {
            return 0; 
        }

        double total = 0;
        for (ItemCarrinho item : itens) {
            double precoItem = item.getPrecoUnitarioNoMomentoDaAdicao(); 
            double quantidadeItem = item.getQuantidade(); 

            total = total + (precoItem * quantidadeItem); 
        }
        return total;
    }

}
