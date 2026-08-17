package com.example.ga8_220501096_aa2_ev01

import com.example.ga8_220501096_aa2_ev01.data.model.*
import org.junit.Assert.*
import org.junit.Test

class CarritoLogicaTest {

    @Test
    fun calcularTotal_carritoVacio_esCero() {
        val carrito = mutableMapOf<Producto, Int>()
        val total = carrito.entries.sumOf { it.key.precio * it.value }
        assertEquals(0.0, total, 0.01)
    }

    @Test
    fun calcularTotal_unProducto() {
        val producto = Producto(1, "Jugo", "Natural", 5000.0, null, null, "Bebidas", true)
        val carrito = mutableMapOf(producto to 2)
        val total = carrito.entries.sumOf { it.key.precio * it.value }
        assertEquals(10000.0, total, 0.01)
    }

    @Test
    fun calcularTotal_multiplesProductos() {
        val p1 = Producto(1, "Jugo", "Natural", 5000.0, null, null, "Bebidas", true)
        val p2 = Producto(2, "Bandeja", "Paisa", 25000.0, null, null, "Tipico", true)
        val p3 = Producto(3, "Postre", "Helado", 8000.0, null, null, "Postres", true)

        val carrito = mutableMapOf(
            p1 to 2,
            p2 to 1,
            p3 to 3
        )
        val total = carrito.entries.sumOf { it.key.precio * it.value }

        val esperado = (5000.0 * 2) + (25000.0 * 1) + (8000.0 * 3)
        assertEquals(esperado, total, 0.01)
    }

    @Test
    fun carrito_agregarProducto() {
        val carrito = mutableMapOf<Producto, Int>()
        val producto = Producto(1, "Hamburguesa", "Doble", 18000.0, null, null, "Fast Food", true)

        carrito[producto] = (carrito[producto] ?: 0) + 2
        assertEquals(2, carrito[producto])

        carrito[producto] = (carrito[producto] ?: 0) + 1
        assertEquals(3, carrito[producto])
    }

    @Test
    fun carrito_eliminarProducto() {
        val producto = Producto(1, "Hamburguesa", "Doble", 18000.0, null, null, "Fast Food", true)
        val carrito = mutableMapOf(producto to 3)

        carrito.remove(producto)
        assertFalse(carrito.containsKey(producto))
        assertTrue(carrito.isEmpty())
    }

    @Test
    fun carrito_disminuirCantidad() {
        val producto = Producto(1, "Hamburguesa", "Doble", 18000.0, null, null, "Fast Food", true)
        val carrito = mutableMapOf(producto to 3)

        val cantidad = carrito[producto] ?: 0
        if (cantidad > 1) {
            carrito[producto] = cantidad - 1
        } else {
            carrito.remove(producto)
        }
        assertEquals(2, carrito[producto])
    }

    @Test
    fun carrito_disminuirUltima_unidad_seElimina() {
        val producto = Producto(1, "Hamburguesa", "Doble", 18000.0, null, null, "Fast Food", true)
        val carrito = mutableMapOf(producto to 1)

        val cantidad = carrito[producto] ?: 0
        if (cantidad > 1) {
            carrito[producto] = cantidad - 1
        } else {
            carrito.remove(producto)
        }
        assertFalse(carrito.containsKey(producto))
    }

    @Test
    fun carrito_convertirAMapaItems() {
        val p1 = Producto(1, "Jugo", "Natural", 5000.0, null, null, "Bebidas", true)
        val p2 = Producto(2, "Bandeja", "Paisa", 25000.0, null, null, "Tipico", true)
        val carrito = mutableMapOf(p1 to 2, p2 to 1)

        val items = carrito.mapKeys { it.key.idProducto ?: 0 }
            .filterKeys { it > 0 }
            .mapValues { it.value }

        assertEquals(2, items.size)
        assertEquals(2, items[1])
        assertEquals(1, items[2])
    }
}

class LoginRequestTest {

    @Test
    fun loginRequest_camposCorrectos() {
        val request = LoginRequest(
            correo = "juan@test.com",
            contrasena = "123456"
        )
        assertEquals("juan@test.com", request.correo)
        assertEquals("123456", request.contrasena)
    }

    @Test
    fun loginRequest_emailVacio() {
        val request = LoginRequest(correo = "", contrasena = "123456")
        assertTrue(request.correo.isEmpty())
    }
}

class RegistroRequestTest {

    @Test
    fun registroRequest_todosLosCampos() {
        val request = RegistroRequest(
            nombre = "Juan",
            apellido = "Perez",
            correo = "juan@test.com",
            contrasena = "123456",
            telefono = "3101234567",
            direccion = "Calle 10 #5-20",
            ciudad = "Bogota"
        )

        assertEquals("Juan", request.nombre)
        assertEquals("Perez", request.apellido)
        assertEquals("juan@test.com", request.correo)
        assertEquals("123456", request.contrasena)
        assertEquals("3101234567", request.telefono)
        assertEquals("Calle 10 #5-20", request.direccion)
        assertEquals("Bogota", request.ciudad)
    }

    @Test
    fun registroRequest_ciudadesValidas() {
        val ciudades = listOf("Bogota", "Medellin", "Cali", "Barranquilla", "Bucaramanga")
        ciudades.forEach { ciudad ->
            val request = RegistroRequest("A", "B", "a@b.com", "pass", "1", "Dir", ciudad)
            assertEquals(ciudad, request.ciudad)
        }
    }
}

class PedidoEstadoTest {

    @Test
    fun estadosPedido_todosValidos() {
        val estados = listOf(
            "PENDIENTE", "CONFIRMADO", "EN_PREPARACION",
            "EN_CAMINO", "ENTREGADO", "CANCELADO"
        )
        assertEquals(6, estados.size)
        estados.forEach { estado ->
            assertTrue("Estado $estado no es valido", estado.isNotBlank())
        }
    }

    @Test
    fun pedido_colorPorEstado() {
        val colores = mapOf(
            "PENDIENTE" to 0xFFFFA726.toInt(),
            "EN_PREPARACION" to 0xFF42A5F5.toInt(),
            "EN_CAMINO" to 0xFFAB47BC.toInt(),
            "ENTREGADO" to 0xFF66BB6A.toInt(),
            "CANCELADO" to 0xFFEF5350.toInt()
        )

        assertEquals(5, colores.size)
        assertNotNull(colores["PENDIENTE"])
        assertNotNull(colores["CANCELADO"])
    }
}
