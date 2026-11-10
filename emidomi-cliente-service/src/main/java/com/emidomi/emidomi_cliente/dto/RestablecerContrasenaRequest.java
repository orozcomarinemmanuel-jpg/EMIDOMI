package com.emidomi.emidomi_cliente.dto;

/** Cuerpo de POST /api/auth/restablecer-contrasena: token del enlace y la nueva contraseña. */
public record RestablecerContrasenaRequest(String token, String nuevaContrasena) {
}
