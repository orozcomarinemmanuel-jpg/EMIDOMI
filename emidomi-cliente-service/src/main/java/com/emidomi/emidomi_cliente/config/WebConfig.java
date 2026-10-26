package com.emidomi.emidomi_cliente.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sirve las fotos de perfil guardadas en la carpeta de uploads.
 * URL pública: /uploads/clientes/{id}/{uuid}.ext
 * (la ruta /uploads/** está abierta en SecurityConfig porque las etiquetas <img>
 * no envían el token JWT; por eso los nombres son UUID aleatorios.)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads-dir:uploads}")
    private String carpetaUploads;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path base = Paths.get(carpetaUploads).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de uploads: " + base, e);
        }
        // toUri() termina en "/" porque la carpeta ya existe; así Spring la trata como directorio.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(base.toUri().toString());
    }
}
