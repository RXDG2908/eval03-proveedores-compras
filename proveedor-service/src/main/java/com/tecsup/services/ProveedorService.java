package com.tecsup.services;

import com.tecsup.dto.ProveedorDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Proveedor;
import com.tecsup.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository repo;

    public List<Proveedor> listar() {
        return repo.findAll();
    }

    public Proveedor obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
    }

    public Proveedor guardar(ProveedorDTO dto) {
        validarRucUnico(dto.getRuc(), null);
        Proveedor proveedor = new Proveedor();
        copiarDatos(proveedor, dto);
        return repo.save(proveedor);
    }

    public Proveedor actualizar(Long id, ProveedorDTO dto) {
        Proveedor proveedor = obtener(id);
        validarRucUnico(dto.getRuc(), id);
        copiarDatos(proveedor, dto);
        return repo.save(proveedor);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }

    // No pueden existir dos proveedores con el mismo RUC
    private void validarRucUnico(String ruc, Long idActual) {
        Proveedor otro = repo.findByRuc(ruc);
        if (otro != null && !otro.getIdProveedor().equals(idActual)) {
            throw new IllegalArgumentException("Ya existe un proveedor con el RUC " + ruc);
        }
    }

    private void copiarDatos(Proveedor proveedor, ProveedorDTO dto) {
        proveedor.setRuc(dto.getRuc());
        proveedor.setRazonSocial(dto.getRazonSocial());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(dto.getCorreo());
    }
}
