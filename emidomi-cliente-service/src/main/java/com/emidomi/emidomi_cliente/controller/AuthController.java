package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.config.JwtUtil;
import com.emidomi.emidomi_cliente.dto.OlvideContrasenaRequest;
import com.emidomi.emidomi_cliente.dto.RestablecerContrasenaRequest;
import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import com.emidomi.emidomi_cliente.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Cliente clienteNuevo) {
        Optional<Cliente> existente = clienteRepository.findByCorreo(clienteNuevo.getCorreo());
        if (existente.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Ya existe un cliente registrado con ese correo.");
        }

        String contrasenaEncriptada = passwordEncoder.encode(clienteNuevo.getContrasena());
        clienteNuevo.setContrasena(contrasenaEncriptada);

        Cliente clienteGuardado = clienteRepository.save(clienteNuevo);

        clienteGuardado.setContrasena(null);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteGuardado);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(loginRequest.getCorreo());

        if (clienteOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error en la autenticación.");
        }

        Cliente cliente = clienteOpt.get();
        boolean coincide = passwordEncoder.matches(loginRequest.getContrasena(), cliente.getContrasena());

        if (coincide) {
            String token = jwtUtil.generarToken(cliente.getCorreo(), cliente.getIdCliente());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error en la autenticación.");
        }
    }

    /** Mensaje único: el mismo exista o no el correo, para no revelar cuentas registradas. */
    private static final String MENSAJE_OLVIDE =
            "Si el correo está registrado, te enviaremos un enlace para restablecer tu contraseña.";

    /**
     * POST /api/auth/olvide-contrasena
     * Body: {"correo": "..."}. Responde siempre 200 con el mismo mensaje.
     */
    @PostMapping("/olvide-contrasena")
    public ResponseEntity<String> olvideContrasena(@RequestBody OlvideContrasenaRequest peticion) {
        passwordResetService.solicitarRecuperacion(peticion.correo());
        return ResponseEntity.ok(MENSAJE_OLVIDE);
    }

    /**
     * POST /api/auth/restablecer-contrasena
     * Body: {"token": "...", "nuevaContrasena": "..."}.
     * Token inválido, vencido o contraseña corta -> 400 con el motivo (lo maneja GlobalExceptionHandler).
     */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<String> restablecerContrasena(@RequestBody RestablecerContrasenaRequest peticion) {
        passwordResetService.restablecerContrasena(peticion.token(), peticion.nuevaContrasena());
        return ResponseEntity.ok("Tu contraseña se cambió correctamente. Ya puedes iniciar sesión.");
    }

    public static class LoginRequest {
        private String correo;
        private String contrasena;

        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }
}
