package com.tecsup.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class CompraDTO {

    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    // @Valid hace que tambien se validen los datos de cada detalle
    @NotNull(message = "Los detalles son obligatorios")
    @Size(min = 1, message = "La compra debe tener al menos un detalle")
    @Valid
    private List<DetalleCompraDTO> detalles;

    public Long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<DetalleCompraDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraDTO> detalles) {
        this.detalles = detalles;
    }
}
