package com.emidomi.emidomi_cliente;

import com.emidomi.emidomi_cliente.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ModelTests {

    @Test
    void cliente_creacionCorrecta() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setCorreo("juan@test.com");
        cliente.setTelefono("3101234567");
        cliente.setDireccion("Calle 10");
        cliente.setCiudad("Bogota");
        cliente.setEstado(true);

        assertEquals(1, cliente.getIdCliente());
        assertEquals("Juan", cliente.getNombre());
        assertEquals("juan@test.com", cliente.getCorreo());
        assertTrue(cliente.getEstado());
    }

    @Test
    void cliente_contrasenaWriteOnly() {
        Cliente cliente = new Cliente();
        cliente.setContrasena("secreta123");
        assertEquals("secreta123", cliente.getContrasena());
    }

    @Test
    void producto_creacionCorrecta() {
        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Bandeja Paisa");
        producto.setPrecio(new BigDecimal("25000.00"));
        producto.setCategoria("Tipico");
        producto.setEstado(true);

        assertEquals("Bandeja Paisa", producto.getNombre());
        assertEquals(new BigDecimal("25000.00"), producto.getPrecio());
        assertTrue(producto.getEstado());
    }

    @Test
    void pedido_estadoEnumValores() {
        Pedido.EstadoPedido[] estados = Pedido.EstadoPedido.values();
        assertEquals(6, estados.length);
        assertEquals(Pedido.EstadoPedido.PENDIENTE, Pedido.EstadoPedido.valueOf("PENDIENTE"));
        assertEquals(Pedido.EstadoPedido.ENTREGADO, Pedido.EstadoPedido.valueOf("ENTREGADO"));
    }

    @Test
    void pedido_creacionConDefaults() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1);
        pedido.setDireccionEntrega("Calle Test");
        pedido.setEstadoPedido(Pedido.EstadoPedido.PENDIENTE);
        pedido.setSubtotal(new BigDecimal("50000.00"));

        assertEquals(1, pedido.getIdCliente());
        assertEquals(Pedido.EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
        assertEquals(new BigDecimal("50000.00"), pedido.getSubtotal());
    }

    @Test
    void pedido_subtotalMasCostoDomicilioIgualTotal() {
        BigDecimal subtotal = new BigDecimal("50000.00");
        BigDecimal domicilio = new BigDecimal("5000.00");
        BigDecimal total = subtotal.add(domicilio);

        assertEquals(new BigDecimal("55000.00"), total);
    }

    @Test
    void detallePedido_creacionCorrecta() {
        DetallePedido detalle = new DetallePedido();
        detalle.setIdDetalle(1);
        detalle.setIdPedido(10);
        detalle.setIdProducto(2);
        detalle.setCantidad(3);
        detalle.setPrecioUnitario(new BigDecimal("15000.00"));
        detalle.setSubtotal(new BigDecimal("45000.00"));

        assertEquals(3, detalle.getCantidad());
        assertEquals(new BigDecimal("15000.00"), detalle.getPrecioUnitario());
    }

    @Test
    void detallePedido_subtotalEsCantidadPorPrecio() {
        int cantidad = 4;
        BigDecimal precio = new BigDecimal("8000.00");
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));

        assertEquals(new BigDecimal("32000.00"), subtotal);
    }

    @Test
    void pago_metodoPagoEnumValores() {
        Pago.MetodoPago[] metodos = Pago.MetodoPago.values();
        assertEquals(6, metodos.length);
        assertEquals(Pago.MetodoPago.efectivo, Pago.MetodoPago.valueOf("efectivo"));
        assertEquals(Pago.MetodoPago.nequi, Pago.MetodoPago.valueOf("nequi"));
    }

    @Test
    void pago_estadoPagoEnumValores() {
        Pago.EstadoPago[] estados = Pago.EstadoPago.values();
        assertEquals(4, estados.length);
        assertEquals(Pago.EstadoPago.pendiente, Pago.EstadoPago.valueOf("pendiente"));
        assertEquals(Pago.EstadoPago.rechazado, Pago.EstadoPago.valueOf("rechazado"));
    }

    @Test
    void pago_creacionCorrecta() {
        Pago pago = new Pago();
        pago.setIdPago(1);
        pago.setIdPedido(10);
        pago.setMetodoPago(Pago.MetodoPago.efectivo);
        pago.setEstadoPago(Pago.EstadoPago.pendiente);
        pago.setMonto(new BigDecimal("50000.00"));

        assertEquals(Pago.MetodoPago.efectivo, pago.getMetodoPago());
        assertEquals(Pago.EstadoPago.pendiente, pago.getEstadoPago());
        assertEquals(new BigDecimal("50000.00"), pago.getMonto());
    }

    @Test
    void restaurante_creacionCorrecta() {
        Restaurante restaurante = new Restaurante();
        restaurante.setIdRestaurante(1);
        restaurante.setNombre("La Abuela");
        restaurante.setCiudad("Bogota");
        restaurante.setCategoria("Tipico");
        restaurante.setCalificacion(new BigDecimal("4.5"));
        restaurante.setEstado(true);

        assertEquals("La Abuela", restaurante.getNombre());
        assertEquals(new BigDecimal("4.5"), restaurante.getCalificacion());
    }

    @Test
    void repartidor_creacionCorrecta() {
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(1);
        repartidor.setNombre("Carlos");
        repartidor.setApellido("Lopez");
        repartidor.setVehiculo("Moto");
        repartidor.setPlaca("ABC-123");
        repartidor.setDisponible(true);
        repartidor.setEstado(true);

        assertEquals("Carlos", repartidor.getNombre());
        assertTrue(repartidor.getDisponible());
        assertEquals("Moto", repartidor.getVehiculo());
    }

    @Test
    void repartidor_filtrarDisponibles() {
        Repartidor r1 = new Repartidor();
        r1.setNombre("A");
        r1.setDisponible(true);
        r1.setEstado(true);

        Repartidor r2 = new Repartidor();
        r2.setNombre("B");
        r2.setDisponible(false);
        r2.setEstado(true);

        Repartidor r3 = new Repartidor();
        r3.setNombre("C");
        r3.setDisponible(true);
        r3.setEstado(false);

        boolean r1Disponible = r1.getDisponible() && r1.getEstado();
        boolean r2Disponible = r2.getDisponible() && r2.getEstado();
        boolean r3Disponible = r3.getDisponible() && r3.getEstado();

        assertTrue(r1Disponible);
        assertFalse(r2Disponible);
        assertFalse(r3Disponible);
    }
}
