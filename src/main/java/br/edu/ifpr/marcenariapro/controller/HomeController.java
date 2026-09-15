package br.edu.ifpr.marcenariapro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "MarcenariaPro API funcionando!";
    }
}