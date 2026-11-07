package com.tecsup.dto;

import jakarta.validation.constraints.*;

public class ProveedorDTO {

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "(10|15|17|20)\\d{9}", message = "El RUC debe tener 11 digitos y empezar con 10, 15, 17 o 20")
    private String ruc;

    @NotBlank(message = "La razon social es obligatoria")
    @Size(min = 3, max = 150, message = "La razon social debe tener entre 3 y 150 caracteres")
    private String razonSocial;

    @Size(max = 200, message = "La direccion no puede pasar de 200 caracteres")
    private String direccion;

    @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
    private String telefono;

    @Email(message = "El correo no es valido")
    @Size(max = 100, message = "El correo no puede pasar de 100 caracteres")
    private String correo;

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
