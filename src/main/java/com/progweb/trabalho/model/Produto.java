package com.progweb.trabalho.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Produto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Coleção é obrigatória")
    private String colecao;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @Min(value = 1, message = "Tamanho deve ser no mínimo 1")
    private int tamanho;

    private String imgUrl;  // imagem pode ser opcional, se quiser validação aqui, pode adicionar

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "Preço deve ser maior que zero")
    private Double preco;

    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private int quantidade;

}
