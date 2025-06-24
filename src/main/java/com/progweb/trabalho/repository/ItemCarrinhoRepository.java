package com.progweb.trabalho.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


import com.progweb.trabalho.model.ItemCarrinho;

@Repository
public interface ItemCarrinhoRepository extends JpaRepository <ItemCarrinho,Long>{

}
