package com.emidomi.emidomi_cliente.service;

/**
 * Se lanza cuando un cliente intenta usar un correo que ya pertenece a OTRO cliente.
 * El controlador la convierte en 409 Conflict.
 */
public class CorreoDuplicadoException extends RuntimeException {

    /** Mismo mensaje que usa el registro de clientes. */
    public static final String MENSAJE = "Ya existe un cliente registrado con ese correo.";

    public CorreoDuplicadoException() {
        super(MENSAJE);
    }
}
