package com.emidomi.emidomi_cliente.dto;

/** Cuerpo de POST /api/auth/olvide-contrasena. Solo lleva el correo del cliente. */
public record OlvideContrasenaRequest(String correo) {
}
