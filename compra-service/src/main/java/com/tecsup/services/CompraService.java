package com.tecsup.services;

import com.tecsup.client.ProductoClient;
import com.tecsup.client.ProveedorClient;
import com.tecsup.dto.CompraDTO;
import com.tecsup.dto.DetalleCompraDTO;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.dto.ProveedorDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Compra;
import com.tecsup.model.DetalleCompra;
import com.tecsup.repository.CompraRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository repo;

    @Autowired
    private ProveedorClient proveedorClient;

    @Autowired
    private ProductoClient productoClient;

    public List<Compra> listar() {
        List<Compra> compras = repo.findAll();
        compras.forEach(compra -> completarDatos(compra));
        return compras;
    }

    public Compra obtener(Long id) {
        Compra compra = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
        completarDatos(compra);
        return compra;
    }

    public Compra registrar(CompraDTO dto) {
        // 1. El proveedor debe existir en proveedor-service
        ProveedorDTO proveedor = buscarProveedor(dto.getIdProveedor());

        Compra compra = new Compra();
        compra.setIdProveedor(dto.getIdProveedor());
        compra.setFecha(dto.getFecha());

        double total = 0;
        for (DetalleCompraDTO item : dto.getDetalles()) {
            if (item == null) {
                throw new IllegalArgumentException("La compra tiene un detalle vacio");
            }

            // 2. Cada producto debe existir y ser de ese proveedor
            ProductoDTO producto = buscarProducto(item.getIdProducto());
            if (!producto.getIdProveedor().equals(dto.getIdProveedor())) {
                throw new IllegalArgumentException("El producto " + producto.getNombre()
                        + " no pertenece al proveedor " + proveedor.getRazonSocial());
            }

            // 3. Subtotal del detalle = cantidad x precio
            double subtotal = redondear(item.getCantidad() * item.getPrecio());

            DetalleCompra detalle = new DetalleCompra();
            detalle.setIdProducto(item.getIdProducto());
            detalle.setNombreProducto(producto.getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(item.getPrecio());
            detalle.setSubtotal(subtotal);
            detalle.setCompra(compra);
            compra.getDetalles().add(detalle);

            total = total + subtotal;
        }

        // 4. Total de la compra = suma de los subtotales
        compra.setTotal(redondear(total));
        Compra guardada = repo.save(compra);
        guardada.setProveedor(proveedor);
        return guardada;
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }

    private ProveedorDTO buscarProveedor(Long idProveedor) {
        try {
            return proveedorClient.obtenerProveedor(idProveedor);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + idProveedor);
        }
    }

    private ProductoDTO buscarProducto(Long idProducto) {
        try {
            return productoClient.obtenerProducto(idProducto);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + idProducto);
        }
    }

    // Agrega el proveedor y el nombre de cada producto antes de responder
    private void completarDatos(Compra compra) {
        try {
            compra.setProveedor(proveedorClient.obtenerProveedor(compra.getIdProveedor()));
        } catch (FeignException.NotFound e) {
            compra.setProveedor(null);
        }
        for (DetalleCompra detalle : compra.getDetalles()) {
            try {
                detalle.setNombreProducto(productoClient.obtenerProducto(detalle.getIdProducto()).getNombre());
            } catch (FeignException.NotFound e) {
                detalle.setNombreProducto("Producto eliminado");
            }
        }
    }

    // Deja el monto con 2 decimales
    private double redondear(double monto) {
        return Math.round(monto * 100) / 100.0;
    }
}
