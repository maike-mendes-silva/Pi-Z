package com.progweb.trabalho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/suporte")
public class SuporteController {

    @GetMapping
    public String index(){
        return "suporte";
    }
}
