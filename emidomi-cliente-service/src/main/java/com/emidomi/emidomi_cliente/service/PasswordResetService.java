package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Recuperación de contraseña en dos pasos:
 *  1) solicitarRecuperacion: genera un token de un solo uso (válido 1 hora) y envía el enlace.
 *  2) restablecerContrasena: valida el token y guarda la nueva contraseña (BCrypt).
 *
 * Seguridad: solicitarRecuperacion NO revela si el correo existe. El controlador
 * responde siempre lo mismo.
 */
@Service
public class PasswordResetService {

    /** Tiempo de validez del enlace de recuperación. */
    public static final int MINUTOS_VALIDEZ = 60;

    /** Longitud mínima de la nueva contraseña. */
    public static final int MIN_LONGITUD_CONTRASENA = 8;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /** Base de la página del frontend donde el cliente escribe la nueva contraseña. */
    @Value("${app.frontend-url:http://127.0.0.1:5500/frontend}")
    private String frontendUrl;

    /**
     * Si el correo existe, genera el token, lo guarda con su vencimiento y envía el enlace.
     * Si no existe, no hace nada. En ambos casos el controlador responde igual.
     */
    @Transactional
    public void solicitarRecuperacion(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        clienteRepository.findByCorreo(correo.trim()).ifPresent(cliente -> {
            String token = UUID.randomUUID().toString();
            cliente.setResetToken(token);
            cliente.setResetTokenExpira(LocalDateTime.now().plusMinutes(MINUTOS_VALIDEZ));
            clienteRepository.save(cliente);

            String enlace = frontendUrl + "/restablecer.html?token=" + token;
            emailService.enviarEnlaceRecuperacion(cliente.getCorreo(), enlace);
        });
    }

    /**
     * Cambia la contraseña usando el token del enlace. Si el token no existe, venció
     * o la contraseña es muy corta, lanza IllegalArgumentException (el handler la
     * convierte en 400 con el mensaje). Al terminar, el token se borra: es de un solo uso.
     */
    @Transactional
    public void restablecerContrasena(String token, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.length() < MIN_LONGITUD_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + MIN_LONGITUD_CONTRASENA + " caracteres.");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El enlace de recuperación no es válido.");
        }

        Cliente cliente = clienteRepository.findByResetToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El enlace de recuperación no es válido o ya fue usado."));

        if (cliente.getResetTokenExpira() == null
                || cliente.getResetTokenExpira().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace de recuperación venció. Solicita uno nuevo.");
        }

        cliente.setContrasena(passwordEncoder.encode(nuevaContrasena));
        cliente.setResetToken(null);
        cliente.setResetTokenExpira(null);
        clienteRepository.save(cliente);
    }
}
