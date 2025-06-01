package com.progweb.trabalho.carrinho;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @GetMapping
    public String index() {
        return "carrinho";
    }

}
