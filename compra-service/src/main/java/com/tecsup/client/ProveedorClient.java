package com.tecsup.client;

import com.tecsup.dto.ProveedorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign busca "proveedor-service" en Eureka; no se escribe la IP ni el puerto
@FeignClient(name = "proveedor-service")
public interface ProveedorClient {

    @GetMapping("/api/proveedores/{id}")
    ProveedorDTO obtenerProveedor(@PathVariable("id") Long id);
}
