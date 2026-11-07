package com.tecsup.client;

import com.tecsup.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign busca "producto-service" en Eureka; no se escribe la IP ni el puerto
@FeignClient(name = "producto-service")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Long id);
}
