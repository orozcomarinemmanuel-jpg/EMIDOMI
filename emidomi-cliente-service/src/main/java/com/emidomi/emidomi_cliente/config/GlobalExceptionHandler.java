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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    /**
     * Errores lanzados con ResponseStatusException (403 de dueño, 404, 413 de tamaño...).
     * Se responde el MOTIVO como texto plano, porque el frontend lo muestra tal cual.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> manejarEstadoHttp(ResponseStatusException e) {
        String motivo = e.getReason() != null ? e.getReason() : "Error en la petición.";
        return ResponseEntity.status(e.getStatusCode()).body(motivo);
    }

    /** Spring rechaza la subida si supera spring.servlet.multipart.max-file-size (5 MB) o max-request-size (6 MB). */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> manejarArchivoGrande(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("La imagen supera el límite de 5 MB.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body("Datos inválidos: campos " + mensaje);
    }
}