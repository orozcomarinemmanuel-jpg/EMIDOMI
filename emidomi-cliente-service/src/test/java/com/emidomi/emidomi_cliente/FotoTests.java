package com.emidomi.emidomi_cliente;

import com.emidomi.emidomi_cliente.config.JwtUtil;
import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de subida de foto de perfil (B3).
 * - Los archivos se guardan en target/test-uploads (no en uploads/ real).
 * - La base de datos se revierte en cada prueba: foto_url vuelve a NULL.
 */
@SpringBootTest(properties = "app.uploads-dir=target/test-uploads")
@AutoConfigureMockMvc
@Transactional
class FotoTests {

    private static final Path CARPETA_PRUEBA = Path.of("target", "test-uploads");

    @Autowired private MockMvc mvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private ClienteRepository clienteRepository;

    @AfterAll
    static void limpiarCarpetaDePrueba() throws Exception {
        if (!Files.exists(CARPETA_PRUEBA)) return;
        try (Stream<Path> rutas = Files.walk(CARPETA_PRUEBA)) {
            rutas.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    // ---------- Generadores de archivos de prueba ----------

    private byte[] imagen(String formato) throws Exception {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, formato, out);
        return out.toByteArray();
    }

    private String tokenDe(int idCliente) {
        Cliente c = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new AssertionError("No existe el cliente de prueba " + idCliente));
        return jwtUtil.generarToken(c.getCorreo(), idCliente);
    }

    private MockMultipartFile archivo(String nombre, byte[] contenido, String tipo) {
        return new MockMultipartFile("foto", nombre, tipo, contenido);
    }

    // ---------- Casos válidos ----------

    @Test
    void png_real_se_acepta_y_se_guarda_con_nombre_uuid() throws Exception {
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("mi-foto.png", imagen("png"), "image/png"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoUrl").value(org.hamcrest.Matchers.matchesPattern(
                        "/uploads/clientes/15/[0-9a-f-]{36}\\.png")));
    }

    @Test
    void el_contenido_manda_sobre_el_nombre_jpg_real_con_nombre_png() throws Exception {
        // Nombre dice .png pero el contenido es JPEG: debe guardarse como .jpg
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("engañosa.png", imagen("jpg"), "image/png"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoUrl").value(org.hamcrest.Matchers.endsWith(".jpg")));
    }

    @Test
    void nombre_malicioso_no_se_usa_en_la_ruta() throws Exception {
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("../../../evil.php.png", imagen("png"), "image/png"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoUrl").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("evil"))))
                .andExpect(jsonPath("$.fotoUrl").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString(".."))));
    }

    // ---------- Casos rechazados ----------

    @Test
    void texto_con_nombre_de_imagen_se_rechaza_400() throws Exception {
        byte[] texto = "esto no es una imagen".getBytes();
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("foto.jpg", texto, "image/jpeg"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cabecera_png_con_datos_corruptos_se_rechaza_400() throws Exception {
        // Firma PNG correcta pero contenido que no se puede decodificar
        byte[] falsa = new byte[64];
        byte[] firma = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        System.arraycopy(firma, 0, falsa, 0, firma.length);
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("falsa.png", falsa, "image/png"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void riff_que_no_es_webp_se_rechaza_400() throws Exception {
        byte[] wav = "RIFF0000WAVEfmt ".getBytes();
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("audio.webp", wav, "image/webp"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void archivo_mayor_a_5_mb_se_rechaza_413() throws Exception {
        byte[] grande = new byte[5 * 1024 * 1024 + 1];
        byte[] firma = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        System.arraycopy(firma, 0, grande, 0, firma.length);
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("gigante.png", grande, "image/png"))
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(content().string("La imagen supera el límite de 5 MB."));
    }

    @Test
    void sin_archivo_se_rechaza() throws Exception {
        mvc.perform(multipart("/api/clientes/15/foto")
                        .header("Authorization", "Bearer " + tokenDe(15)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void cliente_17_no_puede_subir_foto_al_15_403() throws Exception {
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("foto.png", imagen("png"), "image/png"))
                        .header("Authorization", "Bearer " + tokenDe(17)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Solo puedes cambiar tu propia foto."));
    }

    @Test
    void sin_token_no_se_puede_subir() throws Exception {
        mvc.perform(multipart("/api/clientes/15/foto")
                        .file(archivo("foto.png", imagen("png"), "image/png")))
                .andExpect(status().is4xxClientError());
    }
}
