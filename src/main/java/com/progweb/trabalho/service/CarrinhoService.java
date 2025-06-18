package com.progweb.trabalho.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.progweb.trabalho.model.Carrinho;
import com.progweb.trabalho.repository.CarrinhoRepository;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    public Carrinho acharPorId(long id){
        return carrinhoRepository.findByUsuarioId(id);
    }

    public Carrinho salvar(Carrinho carrinho){
        return carrinhoRepository.save(carrinho);
    }

}
