package com.progweb.trabalho.acesso;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @GetMapping
    public String index(){
        return "cadastro";
    }

    @PostMapping("/novo")
    public String novo(){
        return "redirect:/login";
    }

}
