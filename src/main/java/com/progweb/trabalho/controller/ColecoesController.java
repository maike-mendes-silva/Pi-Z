package com.progweb.trabalho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/colecoes")
public class ColecoesController {
    
    @RequestMapping
    public String index() {
        return "colecoes";
    }
    
}
