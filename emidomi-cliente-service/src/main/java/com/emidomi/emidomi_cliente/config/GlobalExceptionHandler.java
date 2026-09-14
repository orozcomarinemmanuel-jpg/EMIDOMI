package com.emidomi.emidomi_cliente.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarArgumentoInvalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> manejarRecursoNoEncontrado(EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso no encontrado.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> manejarViolacionIntegridad(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body("No se pudo guardar: violación de integridad de datos.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> manejarCuerpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("El cuerpo de la petición no es válido o falta información.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body("Datos inválidos: campos " + mensaje);
    }
}