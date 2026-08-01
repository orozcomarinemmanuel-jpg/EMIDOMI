package com.emidomi.emidomi_cliente.controller;

import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Controlador REST dedicado a autenticación: registro e inicio de sesión.
// Lo separamos de ClienteController porque conceptualmente es una
// responsabilidad distinta (seguridad) aunque use la misma tabla.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ClienteRepository clienteRepository;

    // Inyectamos el PasswordEncoder que definimos en SecurityConfig
    // (el bean BCryptPasswordEncoder)
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ------------------------------------------------------------------
    // REGISTRO
    // POST /api/auth/registro
    // ------------------------------------------------------------------
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Cliente clienteNuevo) {

        // Verificamos que no exista ya un cliente con ese correo
        Optional<Cliente> existente = clienteRepository.findByCorreo(clienteNuevo.getCorreo());
        if (existente.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409: el recurso ya existe
                    .body("Ya existe un cliente registrado con ese correo.");
        }

        // Encriptamos la contraseña ANTES de guardarla. Nunca se guarda en texto plano.
        String contrasenaEncriptada = passwordEncoder.encode(clienteNuevo.getContrasena());
        clienteNuevo.setContrasena(contrasenaEncriptada);

        Cliente clienteGuardado = clienteRepository.save(clienteNuevo);

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201: recurso creado
                .body(clienteGuardado);
    }

    // ------------------------------------------------------------------
    // LOGIN
    // POST /api/auth/login
    // ------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(loginRequest.getCorreo());

        // Si no existe el correo, no damos pistas de si el problema es el
        // correo o la contraseña (por seguridad, el mensaje es genérico)
        if (clienteOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401: no autorizado
                    .body("Error en la autenticación.");
        }

        Cliente cliente = clienteOpt.get();

        // Comparamos la contraseña ingresada contra el hash guardado.
        // matches() encripta internamente lo ingresado y lo compara con el hash.
        boolean coincide = passwordEncoder.matches(loginRequest.getContrasena(), cliente.getContrasena());

        if (coincide) {
            return ResponseEntity.ok("Autenticación satisfactoria.");
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error en la autenticación.");
        }
    }

    // Clase interna simple para recibir el body del login (correo + contraseña).
    // No usamos la entidad Cliente completa aquí porque el login solo necesita
    // estos dos datos, no nombre/dirección/etc.
    public static class LoginRequest {
        private String correo;
        private String contrasena;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }
}