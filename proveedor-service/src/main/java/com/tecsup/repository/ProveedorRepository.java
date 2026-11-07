package com.tecsup.repository;

import com.tecsup.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Proveedor findByRuc(String ruc);
}
