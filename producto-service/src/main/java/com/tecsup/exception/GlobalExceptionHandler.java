package com.tecsup.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: datos que no cumplen las validaciones del DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErrores(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    // 404: el registro no existe
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("codigo", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 400: regla de negocio no cumplida
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarReglaNegocio(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("codigo", "400");
        return ResponseEntity.badRequest().body(error);
    }

    // 503: el otro microservicio esta apagado o no responde
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> manejarServicioCaido(FeignException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", "No se pudo comunicar con otro microservicio");
        error.put("codigo", "503");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
