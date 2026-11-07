package com.tecsup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AmbienteController {

    @Value("${empresa.ambiente}")
    private String ambiente;

    @Value("${server.port}")
    private String puerto;

    @Value("${spring.datasource.url}")
    private String baseDeDatos;

    @Autowired
    private Environment environment;

    // Muestra con que perfil esta corriendo el servicio
    @GetMapping("/ambiente")
    public Map<String, String> mostrar() {
        Map<String, String> datos = new HashMap<>();
        datos.put("perfil", environment.getActiveProfiles()[0]);
        datos.put("ambiente", ambiente);
        datos.put("puerto", puerto);
        datos.put("baseDeDatos", baseDeDatos);
        return datos;
    }
}
