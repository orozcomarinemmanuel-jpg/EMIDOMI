package com.emidomi.emidomi_cliente;

import com.emidomi.emidomi_cliente.config.JwtUtil;
import com.emidomi.emidomi_cliente.dto.ConfirmarPedidoRequest;
import com.emidomi.emidomi_cliente.dto.ConfirmarPedidoResponse;
import com.emidomi.emidomi_cliente.dto.GrupoPedidoRequest;
import com.emidomi.emidomi_cliente.dto.ItemPedidoRequest;
import com.emidomi.emidomi_cliente.dto.PedidoConfirmadoResponse;
import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.model.Producto;
import com.emidomi.emidomi_cliente.model.Restaurante;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import com.emidomi.emidomi_cliente.repository.PedidoRepository;
import com.emidomi.emidomi_cliente.repository.ProductoRepository;
import com.emidomi.emidomi_cliente.repository.RestauranteRepository;
import com.emidomi.emidomi_cliente.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de confirmación de pedidos. Cada prueba corre dentro de una transacción
 * que se revierte al final: no queda ningún dato de prueba en db_domicilios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CheckoutTests {

    @Autowired private CheckoutService checkoutService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private MockMvc mvc;

    private Cliente cliente;
    private Restaurante abiertoA;
    private Restaurante abiertoB;
    private Restaurante cerrado;
    private Producto hamburguesaA;   // abiertoA, 28000, disponible
    private Producto gaseosaA;       // abiertoA, 4000, disponible
    private Producto pizzaB;         // abiertoB, 14000, disponible
    private Producto pizzaNoDispB;   // abiertoB, 20000, NO disponible
    private Producto ensaladaCerrado; // cerrado, 9000, disponible

    @BeforeEach
    void crearDatos() {
        cliente = crearCliente();

        abiertoA = guardarRestaurante("Restaurante A", true);
        abiertoB = guardarRestaurante("Restaurante B", true);
        cerrado = guardarRestaurante("Restaurante Cerrado", false);

        hamburguesaA = guardarProducto(abiertoA, "Hamburguesa", "28000.00", true);
        gaseosaA = guardarProducto(abiertoA, "Gaseosa", "4000.00", true);
        pizzaB = guardarProducto(abiertoB, "Pizza", "14000.00", true);
        pizzaNoDispB = guardarProducto(abiertoB, "Pizza Especial", "20000.00", false);
        ensaladaCerrado = guardarProducto(cerrado, "Ensalada", "9000.00", true);
    }

    private Cliente crearCliente() {
        Cliente c = new Cliente();
        c.setNombre("Prueba");
        c.setApellido("Checkout");
        c.setCorreo("t-" + UUID.randomUUID() + "@test.com");
        c.setContrasena("x");
        c.setTelefono("3000000000");
        c.setDireccion("Calle 1 #2-3");
        c.setCiudad("Pereira");
        c.setEstado(true);
        return clienteRepository.save(c);
    }

    private Restaurante guardarRestaurante(String nombre, boolean estado) {
        Restaurante r = new Restaurante();
        r.setNombre(nombre);
        r.setDireccion("Cra 1");
        r.setCiudad("Pereira");
        r.setTelefono("3111111111");
        r.setCorreo("r-" + UUID.randomUUID() + "@test.com");
        r.setCategoria("Prueba");
        r.setCalificacion(new BigDecimal("4.00"));
        r.setEstado(estado);
        return restauranteRepository.save(r);
    }

    private Producto guardarProducto(Restaurante r, String nombre, String precio, boolean disponible) {
        Producto p = new Producto();
        p.setIdRestaurante(r.getIdRestaurante());
        p.setNombre(nombre);
        p.setDescripcion("Prueba");
        p.setPrecio(new BigDecimal(precio));
        p.setCategoria("Platos principales");
        p.setEstado(disponible);
        return productoRepository.save(p);
    }

    private ConfirmarPedidoRequest peticion(String metodo, GrupoPedidoRequest... grupos) {
        return new ConfirmarPedidoRequest("Calle 5 #10-20", "Pereira", "Sin cebolla", metodo, List.of(grupos));
    }

    private GrupoPedidoRequest grupo(Restaurante r, Producto p, int cantidad) {
        return new GrupoPedidoRequest(r.getIdRestaurante(), List.of(new ItemPedidoRequest(p.getIdProducto(), cantidad)));
    }

    private int pedidosDelCliente() {
        return pedidoRepository.findByIdCliente(cliente.getIdCliente()).size();
    }

    // ---------- Casos de éxito ----------

    @Test
    void crea_un_pedido_por_restaurante_con_totales_calculados_en_servidor() {
        ConfirmarPedidoRequest req = peticion("nequi",
                new GrupoPedidoRequest(abiertoA.getIdRestaurante(), List.of(
                        new ItemPedidoRequest(hamburguesaA.getIdProducto(), 2),
                        new ItemPedidoRequest(gaseosaA.getIdProducto(), 1))),
                grupo(abiertoB, pizzaB, 1));

        ConfirmarPedidoResponse r = checkoutService.confirmar(cliente.getIdCliente(), req);

        assertEquals(2, r.pedidos().size());
        assertEquals(0, new BigDecimal("84000.00").compareTo(r.totalGeneral())); // (60000+5000) + (14000+5000)

        PedidoConfirmadoResponse a = r.pedidos().get(0);
        assertEquals("Restaurante A", a.nombreRestaurante());
        assertEquals(0, new BigDecimal("60000.00").compareTo(a.subtotal()));
        assertEquals(0, new BigDecimal("5000.00").compareTo(a.costoDomicilio()));
        assertEquals(0, new BigDecimal("65000.00").compareTo(a.total()));
        assertEquals("PENDIENTE", a.estadoPedido());
        assertNull(a.idRepartidor());
        assertEquals("aprobado", a.pago().estadoPago());
        assertEquals("nequi", a.pago().metodoPago());
        assertTrue(a.pago().referencia().startsWith("EMD-" + a.idPedido() + "-"));

        assertEquals(2, pedidosDelCliente());
    }

    @Test
    void efectivo_y_transferencia_quedan_pendientes() {
        ConfirmarPedidoResponse efectivo = checkoutService.confirmar(cliente.getIdCliente(),
                peticion("efectivo", grupo(abiertoA, gaseosaA, 1)));
        assertEquals("pendiente", efectivo.pedidos().get(0).pago().estadoPago());

        ConfirmarPedidoResponse transf = checkoutService.confirmar(cliente.getIdCliente(),
                peticion("transferencia", grupo(abiertoB, pizzaB, 1)));
        assertEquals("pendiente", transf.pedidos().get(0).pago().estadoPago());
    }

    @Test
    void el_total_enviado_por_el_navegador_se_ignora() {
        // El DTO no tiene campos de precio ni total: aunque el JSON los traiga, no llegan al servicio.
        ConfirmarPedidoResponse r = checkoutService.confirmar(cliente.getIdCliente(),
                peticion("daviplata", grupo(abiertoA, gaseosaA, 3)));
        assertEquals(0, new BigDecimal("17000.00").compareTo(r.pedidos().get(0).total())); // 12000+5000
    }

    // ---------- Casos de error: nada debe quedar a medias ----------

    @Test
    void restaurante_cerrado_rechaza_todo_el_carrito() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi",
                        grupo(abiertoA, gaseosaA, 1),
                        grupo(cerrado, ensaladaCerrado, 1))));
        assertTrue(ex.getMessage().contains("Restaurante Cerrado"));
        assertTrue(ex.getMessage().contains("está cerrado"));
        assertEquals(0, pedidosDelCliente());
    }

    @Test
    void producto_no_disponible_bloquea_todo_y_nombra_el_producto() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi",
                        grupo(abiertoA, gaseosaA, 1),
                        grupo(abiertoB, pizzaNoDispB, 1))));
        assertTrue(ex.getMessage().contains("Pizza Especial"));
        assertTrue(ex.getMessage().contains("no está disponible"));
        assertEquals(0, pedidosDelCliente());
    }

    @Test
    void producto_de_otro_restaurante_se_rechaza() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi",
                        grupo(abiertoB, hamburguesaA, 1))));
        assertTrue(ex.getMessage().contains("no pertenece"));
        assertEquals(0, pedidosDelCliente());
    }

    @Test
    void metodo_de_pago_invalido_se_rechaza() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("bitcoin", grupo(abiertoA, gaseosaA, 1))));
        assertTrue(ex.getMessage().contains("Método de pago no válido"));
        assertEquals(0, pedidosDelCliente());
    }

    @Test
    void cliente_inactivo_no_puede_comprar() {
        cliente.setEstado(false);
        clienteRepository.save(cliente);
        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi", grupo(abiertoA, gaseosaA, 1))));
    }

    @Test
    void cantidad_fuera_de_rango_se_rechaza() {
        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi", grupo(abiertoA, gaseosaA, 0))));
        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi", grupo(abiertoA, gaseosaA, 100))));
    }

    // ---------- Listado de pedidos del cliente (B3) ----------

    @Test
    void listarMios_devuelve_solo_los_pedidos_del_cliente_mas_recientes_primero() {
        Cliente otro = crearCliente();
        ConfirmarPedidoResponse primero = checkoutService.confirmar(cliente.getIdCliente(),
                peticion("efectivo", grupo(abiertoA, gaseosaA, 1)));
        ConfirmarPedidoResponse segundo = checkoutService.confirmar(cliente.getIdCliente(),
                peticion("nequi", grupo(abiertoA, hamburguesaA, 1)));
        checkoutService.confirmar(otro.getIdCliente(), peticion("nequi", grupo(abiertoB, pizzaB, 1)));

        List<com.emidomi.emidomi_cliente.dto.PedidoClienteResponse> mios = checkoutService.listarMios(cliente.getIdCliente());

        assertEquals(2, mios.size());
        assertEquals(segundo.pedidos().get(0).idPedido(), mios.get(0).idPedido()); // el más reciente primero
        assertEquals(primero.pedidos().get(0).idPedido(), mios.get(1).idPedido());

        var reciente = mios.get(0);
        assertEquals("Restaurante A", reciente.nombreRestaurante());
        assertEquals("PENDIENTE", reciente.estadoPedido());
        assertEquals(1, reciente.productos().size());
        assertEquals("Hamburguesa", reciente.productos().get(0).nombre());
        assertEquals(0, new BigDecimal("28000.00").compareTo(reciente.productos().get(0).precioUnitario()));
        assertEquals("aprobado", reciente.pago().estadoPago());

        assertEquals("pendiente", mios.get(1).pago().estadoPago()); // el de efectivo
    }

    @Test
    void listarMios_sin_pedidos_devuelve_lista_vacia() {
        assertTrue(checkoutService.listarMios(crearCliente().getIdCliente()).isEmpty());
    }

    @Test
    void get_mios_sin_token_no_responde_datos() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/pedidos/mios"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void get_mios_con_token_devuelve_solo_los_pedidos_de_ese_cliente() throws Exception {
        Cliente otro = crearCliente();
        checkoutService.confirmar(cliente.getIdCliente(), peticion("nequi", grupo(abiertoA, gaseosaA, 1)));
        checkoutService.confirmar(otro.getIdCliente(), peticion("nequi", grupo(abiertoB, pizzaB, 1)));

        String tokenA = jwtUtil.generarToken(cliente.getCorreo(), cliente.getIdCliente());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/pedidos/mios")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombreRestaurante").value("Restaurante A"));
    }

    @Test
    void get_pedidos_por_cliente_solo_permite_el_propio_id() throws Exception {
        Cliente otro = crearCliente();
        String tokenA = jwtUtil.generarToken(cliente.getCorreo(), cliente.getIdCliente());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/pedidos/cliente/" + otro.getIdCliente())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isForbidden());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/pedidos/cliente/" + cliente.getIdCliente())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    // ---------- Endpoint HTTP: autenticación y contrato JSON ----------

    @Test
    void sin_token_no_se_puede_confirmar() throws Exception {
        mvc.perform(post("/api/pedidos/confirmar")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void con_token_confirma_y_responde_201_ignorando_totales_del_cliente() throws Exception {
        String token = jwtUtil.generarToken(cliente.getCorreo(), cliente.getIdCliente());
        String cuerpo = """
                {
                  "direccionEntrega": "Calle 5 #10-20",
                  "ciudadEntrega": "Pereira",
                  "notas": "",
                  "metodoPago": "tarjeta_debito",
                  "total": 1,
                  "subtotal": 1,
                  "grupos": [
                    { "idRestaurante": %d, "items": [ { "idProducto": %d, "cantidad": 2 } ] }
                  ]
                }
                """.formatted(abiertoA.getIdRestaurante(), hamburguesaA.getIdProducto());

        mvc.perform(post("/api/pedidos/confirmar")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidos[0].total").value(61000.00))
                .andExpect(jsonPath("$.pedidos[0].pago.estadoPago").value("aprobado"));
    }

    @Test
    void con_token_y_restaurante_cerrado_responde_400_con_mensaje_claro() throws Exception {
        String token = jwtUtil.generarToken(cliente.getCorreo(), cliente.getIdCliente());
        String cuerpo = """
                { "direccionEntrega": "Calle 5", "metodoPago": "nequi",
                  "grupos": [ { "idRestaurante": %d, "items": [ { "idProducto": %d, "cantidad": 1 } ] } ] }
                """.formatted(cerrado.getIdRestaurante(), ensaladaCerrado.getIdProducto());

        mvc.perform(post("/api/pedidos/confirmar")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().string(org.hamcrest.Matchers.containsString("Restaurante Cerrado")));
    }
}
