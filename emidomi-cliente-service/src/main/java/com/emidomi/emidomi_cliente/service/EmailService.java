package com.emidomi.emidomi_cliente.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envía correos de la aplicación (por ahora, el enlace de recuperación de contraseña).
 *
 * El JavaMailSender solo existe si hay spring.mail.host configurado. Si no existe,
 * o si el envío falla, el enlace se escribe en el log del backend y NO se lanza error:
 * así el flujo funciona en desarrollo sin servidor de correo.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String remitenteConfigurado;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    /**
     * Envía el enlace para restablecer la contraseña. Nunca lanza excepción por fallo de correo.
     */
    public void enviarEnlaceRecuperacion(String destinatario, String enlace) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            // SMTP no configurado: solo dejamos el enlace en el log (modo desarrollo)
            log.warn("SMTP no configurado. Enlace de recuperación para {}: {}", destinatario, enlace);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitenteConfigurado == null || remitenteConfigurado.isBlank()
                ? "no-responder@emidomi.local"
                : remitenteConfigurado);
        mensaje.setTo(destinatario);
        mensaje.setSubject("EmiDomi - Recupera tu contraseña");
        mensaje.setText("Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n"
                + "Abre este enlace (vence en 1 hora):\n\n"
                + enlace + "\n\n"
                + "Si no fuiste tú, ignora este correo.");
        try {
            sender.send(mensaje);
        } catch (MailException e) {
            // No se revela al usuario que el envío falló; el enlace queda en el log
            log.error("No se pudo enviar el correo de recuperación a {}: {}", destinatario, e.getMessage());
            log.warn("Enlace de recuperación para {}: {}", destinatario, enlace);
        }
    }
}
