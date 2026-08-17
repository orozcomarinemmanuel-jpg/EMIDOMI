package com.example.ga8_220501096_aa2_ev01

import com.example.ga8_220501096_aa2_ev01.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ProductoModelTest {

    @Test
    fun producto_creacionBasica() {
        val producto = Producto(
            idProducto = 1,
            nombre = "Bandeja Paisa",
            descripcion = "Plato típico colombiano",
            precio = 25000.0,
            imagen = null,
            emoji = "🍽️",
            categoria = "Tipico",
            estado = true
        )

        assertEquals(1, producto.idProducto)
        assertEquals("Bandeja Paisa", producto.nombre)
        assertEquals(25000.0, producto.precio, 0.01)
        assertTrue(producto.estado!!)
    }

    @Test
    fun producto_productoNoDisponible() {
        val producto = Producto(
            idProducto = 2,
            nombre = "Agua",
            descripcion = "Botella 600ml",
            precio = 3000.0,
            imagen = null,
            emoji = null,
            categoria = "Bebidas",
            estado = false
        )
        assertFalse(producto.estado!!)
    }

    @Test
    fun producto_categoriasDiferentes() {
        val categorias = listOf("Tipico", "Bebidas", "Postres", "Fast Food", "Parrilla")

        categorias.forEach { cat ->
            val producto = Producto(
                idProducto = 1,
                nombre = "Test",
                descripcion = "Test",
                precio = 10000.0,
                imagen = null,
                emoji = null,
                categoria = cat,
                estado = true
            )
            assertEquals(cat, producto.categoria)
        }
    }
}

class DetallePedidoModelTest {

    @Test
    fun detallePedido_creacionCorrecta() {
        val detalle = DetallePedido(
            idDetalle = 1,
            idPedido = 10,
            idProducto = 2,
            cantidad = 3,
            precioUnitario = 15000.0,
            subtotal = 45000.0
        )

        assertEquals(1, detalle.idDetalle)
        assertEquals(10, detalle.idPedido)
        assertEquals(2, detalle.idProducto)
        assertEquals(3, detalle.cantidad)
        assertEquals(15000.0, detalle.precioUnitario!!, 0.01)
        assertEquals(45000.0, detalle.subtotal!!, 0.01)
    }

    @Test
    fun detallePedido_subtotalEsCantidadPorPrecio() {
        val cantidad = 4
        val precioUnitario = 8000.0
        val subtotalEsperado = cantidad * precioUnitario

        val detalle = DetallePedido(
            idDetalle = null,
            idPedido = 1,
            idProducto = 1,
            cantidad = cantidad,
            precioUnitario = precioUnitario,
            subtotal = subtotalEsperado
        )

        assertEquals(subtotalEsperado, detalle.subtotal!!, 0.01)
    }

    @Test
    fun detallePedido_request_campoEsenciales() {
        val request = DetallePedidoRequest(
            idProducto = 5,
            cantidad = 2
        )

        assertEquals(5, request.idProducto)
        assertEquals(2, request.cantidad)
    }
}

class RestauranteModelTest {

    @Test
    fun restaurante_creacionCompleta() {
        val restaurante = Restaurante(
            idRestaurante = 1,
            nombre = "La Abuela",
            direccion = "Cra 5 #10-20",
            ciudad = "Bogota",
            telefono = "3101234567",
            correo = "contacto@abuela.com",
            categoria = "Tipico",
            calificacion = 4.5,
            estado = true,
            fechaRegistro = "2026-01-01"
        )

        assertEquals(1, restaurante.idRestaurante)
        assertEquals("La Abuela", restaurante.nombre)
        assertEquals(4.5, restaurante.calificacion!!, 0.01)
        assertTrue(restaurante.estado!!)
    }

    @Test
    fun restaurante_calificacionesEnRango() {
        val calificaciones = listOf(1.0, 2.5, 3.0, 4.0, 4.5, 5.0)
        calificaciones.forEach { cal ->
            assertTrue("Calificacion $cal debe ser >= 1", cal >= 1.0)
            assertTrue("Calificacion $cal debe ser <= 5", cal <= 5.0)
        }
    }
}

class RepartidorModelTest {

    @Test
    fun repartidor_creacionCompleta() {
        val repartidor = Repartidor(
            idRepartidor = 1,
            nombre = "Carlos",
            apellido = "Lopez",
            correo = "carlos@email.com",
            telefono = "3119876543",
            numeroLicencia = "LIC-001",
            vehiculo = "Moto",
            placa = "ABC-123",
            disponible = true,
            estado = true,
            fechaRegistro = "2026-01-15"
        )

        assertEquals("Carlos", repartidor.nombre)
        assertEquals("Lopez", repartidor.apellido)
        assertTrue(repartidor.disponible!!)
        assertTrue(repartidor.estado!!)
        assertEquals("Moto", repartidor.vehiculo)
    }

    @Test
    fun repartidor_filtrarDisponibles() {
        val repartidores = listOf(
            Repartidor(1, "A", "B", "a@b.com", "1", "L1", "Moto", "P1", true, true, null),
            Repartidor(2, "C", "D", "c@d.com", "2", "L2", "Bici", "P2", false, true, null),
            Repartidor(3, "E", "F", "e@f.com", "3", "L3", "Carro", "P3", true, false, null),
        )

        val disponibles = repartidores.filter { it.disponible == true && it.estado == true }
        assertEquals(1, disponibles.size)
        assertEquals("A", disponibles[0].nombre)
    }
}
