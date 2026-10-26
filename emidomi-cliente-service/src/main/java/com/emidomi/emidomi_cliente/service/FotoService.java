package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Guarda la foto de perfil de un cliente.
 *
 * Seguridad:
 * - El TIPO se decide por el CONTENIDO del archivo (firma de bytes), nunca por el
 *   nombre ni por el Content-Type que manda el navegador.
 * - El NOMBRE que guardamos lo genera el servidor (UUID + extensión según el contenido).
 *   El nombre original del usuario no se usa en ninguna ruta, así que no hay
 *   path traversal (../) ni nombres maliciosos (evil.php.png).
 * - Aun así, se comprueba que la ruta final quede dentro de la carpeta de uploads.
 */
@Service
public class FotoService {

    /** Tamaño máximo de la foto: 5 MB. Debe coincidir con spring.servlet.multipart.max-file-size. */
    public static final long TAMANO_MAXIMO_BYTES = 5L * 1024 * 1024;

    private final ClienteRepository clienteRepository;
    private final Path carpetaBase;

    public FotoService(ClienteRepository clienteRepository,
                       @Value("${app.uploads-dir:uploads}") String carpetaUploads) {
        this.clienteRepository = clienteRepository;
        this.carpetaBase = Paths.get(carpetaUploads).toAbsolutePath().normalize();
    }

    public Path getCarpetaBase() {
        return carpetaBase;
    }

    /**
     * Valida la imagen, la guarda en uploads/clientes/{id}/{uuid}.ext, actualiza foto_url
     * y borra la foto anterior. Devuelve la ruta pública (ej. /uploads/clientes/15/uuid.png).
     */
    public String guardarFoto(Integer idCliente, MultipartFile archivo) throws IOException {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado."));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen.");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "La imagen supera el límite de 5 MB.");
        }

        byte[] bytes = archivo.getBytes();
        if (bytes.length > TAMANO_MAXIMO_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "La imagen supera el límite de 5 MB.");
        }

        String extension = detectarExtension(bytes);
        if (extension == null) {
            throw new IllegalArgumentException("El archivo no es una imagen válida. Solo se aceptan JPG, PNG o WebP.");
        }
        // Para JPG y PNG comprobamos además que Java pueda decodificarla de verdad.
        // WebP no lo soporta el JDK por defecto; en ese caso la firma es la única comprobación.
        if (("jpg".equals(extension) || "png".equals(extension)) && !sePuedeDecodificar(bytes)) {
            throw new IllegalArgumentException("La imagen está dañada o no se puede leer.");
        }

        // Nombre generado por el servidor: el nombre original del usuario nunca se usa.
        String nombreArchivo = UUID.randomUUID() + "." + extension;
        Path carpetaCliente = carpetaBase.resolve("clientes").resolve(String.valueOf(idCliente));
        Path destino = carpetaCliente.resolve(nombreArchivo).normalize();
        if (!destino.startsWith(carpetaBase)) {
            throw new IllegalStateException("Ruta de guardado fuera de la carpeta de uploads.");
        }

        Files.createDirectories(carpetaCliente);
        Files.write(destino, bytes);

        String rutaPublica = "/uploads/clientes/" + idCliente + "/" + nombreArchivo;
        String fotoAnterior = cliente.getFotoUrl();
        cliente.setFotoUrl(rutaPublica);
        clienteRepository.save(cliente);

        borrarFotoAnterior(fotoAnterior);
        return rutaPublica;
    }

    /** Detecta el tipo por la firma (primeros bytes) del archivo. Devuelve null si no es JPG/PNG/WebP. */
    static String detectarExtension(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        // WebP: "RIFF" en los bytes 0-3 y "WEBP" en los bytes 8-11
        if (b.length >= 12 && ascii(b, 0, "RIFF") && ascii(b, 8, "WEBP")) {
            return "webp";
        }
        return null;
    }

    private static boolean ascii(byte[] b, int desde, String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (b[desde + i] != (byte) texto.charAt(i)) return false;
        }
        return true;
    }

    private boolean sePuedeDecodificar(byte[] bytes) {
        try {
            BufferedImage imagen = ImageIO.read(new ByteArrayInputStream(bytes));
            return imagen != null && imagen.getWidth() > 0 && imagen.getHeight() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Borra la foto anterior SOLO si su ruta apunta dentro de la carpeta de uploads.
     * Así nunca se borra un archivo ajeno aunque la base de datos tuviera una ruta rara.
     */
    private void borrarFotoAnterior(String rutaPublica) {
        if (rutaPublica == null || !rutaPublica.startsWith("/uploads/")) return;
        Path anterior = carpetaBase.resolve(rutaPublica.substring("/uploads/".length())).normalize();
        if (!anterior.startsWith(carpetaBase)) return;
        try {
            Files.deleteIfExists(anterior);
        } catch (IOException e) {
            // Si no se puede borrar, la foto nueva ya está guardada: no es un error para el cliente.
        }
    }
}
