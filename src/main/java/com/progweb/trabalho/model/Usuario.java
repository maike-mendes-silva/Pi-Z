package com.progweb.trabalho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Column
    private String nomeCompleto;
    
    @NotBlank(message = "Email é obrigatório")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Senha é obrigatório")
    @Column(length = 60)
    private String senha;

    @Column
    private boolean ehAdmin;

    @Column
    private String imgUrl; 

}
