package com.emidomi.emidomi_cliente;

import com.emidomi.emidomi_cliente.config.JwtUtil;
import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprueba que PUT /api/clientes/{id} solo deja editarse a uno mismo.
 * Usa clientes reales de db_domicilios (15, 17 y 18). Cada prueba corre en una
 * transacción que se revierte: los datos de esos clientes no cambian.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteOwnershipTests {

    @Autowired private MockMvc mvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private ClienteRepository clienteRepository;

    /** Cuerpo PUT con los mismos datos que tiene hoy el cliente (no cambia nada). */
    private String cuerpoActual(Cliente c) {
        return """
                {"nombre":"%s","apellido":"%s","correo":"%s","telefono":"%s","direccion":"%s","ciudad":"%s"}
                """.formatted(c.getNombre(), c.getApellido(), c.getCorreo(),
                c.getTelefono(), c.getDireccion(), c.getCiudad());
    }

    private Cliente cargar(int id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new AssertionError("No existe el cliente de prueba con id " + id));
    }

    @Test
    void cliente_15_puede_editarse_a_si_mismo_200() throws Exception {
        Cliente c15 = cargar(15);
        String token = jwtUtil.generarToken(c15.getCorreo(), 15);

        mvc.perform(put("/api/clientes/15")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(cuerpoActual(c15)))
                .andExpect(status().isOk());
    }

    @Test
    void cliente_15_no_puede_editar_al_17_403() throws Exception {
        Cliente c15 = cargar(15);
        Cliente c17 = cargar(17);
        String tokenDe15 = jwtUtil.generarToken(c15.getCorreo(), 15);
        String telefonoAntes = c17.getTelefono();

        mvc.perform(put("/api/clientes/17")
                        .header("Authorization", "Bearer " + tokenDe15)
                        .contentType("application/json")
                        .content(cuerpoActual(c17)))
                .andExpect(status().isForbidden());

        // Y el cliente 17 no cambió
        assertEquals(telefonoAntes, cargar(17).getTelefono());
    }

    @Test
    void cliente_18_no_puede_editar_al_15_403() throws Exception {
        Cliente c15 = cargar(15);
        String tokenDe18 = jwtUtil.generarToken(cargar(18).getCorreo(), 18);

        mvc.perform(put("/api/clientes/15")
                        .header("Authorization", "Bearer " + tokenDe18)
                        .contentType("application/json")
                        .content(cuerpoActual(c15)))
                .andExpect(status().isForbidden());
    }

    /** Cuerpo PUT con los datos actuales pero con otro correo. */
    private String cuerpoConCorreo(Cliente c, String correo) {
        return """
                {"nombre":"%s","apellido":"%s","correo":"%s","telefono":"%s","direccion":"%s","ciudad":"%s"}
                """.formatted(c.getNombre(), c.getApellido(), correo,
                c.getTelefono(), c.getDireccion(), c.getCiudad());
    }

    @Test
    void correo_propio_se_permite_aunque_cambie_mayusculas_200() throws Exception {
        Cliente c15 = cargar(15);
        String token = jwtUtil.generarToken(c15.getCorreo(), 15);

        mvc.perform(put("/api/clientes/15")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(cuerpoConCorreo(c15, c15.getCorreo().toUpperCase())))
                .andExpect(status().isOk());
    }

    @Test
    void correo_de_otro_cliente_responde_409_con_mensaje_claro() throws Exception {
        Cliente c15 = cargar(15);
        Cliente c17 = cargar(17);
        String token = jwtUtil.generarToken(c15.getCorreo(), 15);

        mvc.perform(put("/api/clientes/15")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(cuerpoConCorreo(c15, c17.getCorreo())))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un cliente registrado con ese correo."));

        // El cliente 15 no cambió su correo
        assertEquals(c15.getCorreo(), cargar(15).getCorreo());
    }

    @Test
    void sin_token_no_se_puede_editar() throws Exception {
        Cliente c15 = cargar(15);
        mvc.perform(put("/api/clientes/15")
                        .contentType("application/json")
                        .content(cuerpoActual(c15)))
                .andExpect(status().is4xxClientError());
    }
}
