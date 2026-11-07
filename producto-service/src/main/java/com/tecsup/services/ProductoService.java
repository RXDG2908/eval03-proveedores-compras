package com.tecsup.services;

import com.tecsup.client.ProveedorClient;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.dto.ProveedorDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repo;

    @Autowired
    private ProveedorClient proveedorClient;

    public List<Producto> listar() {
        List<Producto> productos = repo.findAll();
        productos.forEach(producto -> agregarProveedor(producto));
        return productos;
    }

    public Producto obtener(Long id) {
        Producto producto = buscar(id);
        agregarProveedor(producto);
        return producto;
    }

    public Producto guardar(ProductoDTO dto) {
        ProveedorDTO proveedor = verificarProveedor(dto.getIdProveedor());
        Producto producto = new Producto();
        copiarDatos(producto, dto);
        producto = repo.save(producto);
        producto.setProveedor(proveedor);
        return producto;
    }

    public Producto actualizar(Long id, ProductoDTO dto) {
        Producto producto = buscar(id);
        ProveedorDTO proveedor = verificarProveedor(dto.getIdProveedor());
        copiarDatos(producto, dto);
        producto = repo.save(producto);
        producto.setProveedor(proveedor);
        return producto;
    }

    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    private Producto buscar(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    // Pregunta a proveedor-service si el proveedor existe
    private ProveedorDTO verificarProveedor(Long idProveedor) {
        try {
            return proveedorClient.obtenerProveedor(idProveedor);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + idProveedor);
        }
    }

    // Completa el producto con los datos de su proveedor (si el proveedor ya no existe, queda vacio)
    private void agregarProveedor(Producto producto) {
        try {
            producto.setProveedor(proveedorClient.obtenerProveedor(producto.getIdProveedor()));
        } catch (FeignException.NotFound e) {
            producto.setProveedor(null);
        }
    }

    private void copiarDatos(Producto producto, ProductoDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setIdProveedor(dto.getIdProveedor());
    }
}
