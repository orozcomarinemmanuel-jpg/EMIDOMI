package com.emidomi.emidomi_cliente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration le dice a Spring que esta clase define beans (objetos que Spring administra)
@Configuration
public class SecurityConfig {

    // Este bean define cómo se van a encriptar y verificar las contraseñas.
    // BCrypt es un algoritmo diseñado específicamente para contraseñas: es lento
    // a propósito (para dificultar ataques de fuerza bruta) e incluye "salt" automático,
    // por lo que dos contraseñas iguales generan hashes distintos.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Este bean define las reglas de seguridad de la aplicación:
    // qué rutas son públicas, cuáles requieren autenticación, etc.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF porque es una API REST sin sesiones ni formularios HTML
                // tradicionales; CSRF aplica principalmente cuando el navegador maneja cookies
                // de sesión automáticamente.
                .csrf(csrf -> csrf.disable())

                // Definimos qué rutas necesitan autenticación y cuáles no
                .authorizeHttpRequests(auth -> auth
                        // Registro y login deben ser accesibles sin estar autenticado todavía
                        .requestMatchers("/api/auth/**").permitAll()
                        // Por ahora, dejamos el resto de la API (Cliente CRUD) también abierta,
                        // ya que esta evidencia no pide un sistema de sesiones/tokens completo
                        .anyRequest().permitAll()
                )

                // Desactivamos el formulario de login por defecto que trae Spring Security,
                // ya que nosotros manejamos el login manualmente en AuthController
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}