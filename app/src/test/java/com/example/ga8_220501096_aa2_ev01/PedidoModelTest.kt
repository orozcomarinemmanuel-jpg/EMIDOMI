package com.example.ga8_220501096_aa2_ev01

import com.example.ga8_220501096_aa2_ev01.data.model.*
import org.junit.Assert.*
import org.junit.Test

class PedidoModelTest {

    @Test
    fun pedidoRequest_campoTotal_seGuardaCorrectamente() {
        val request = PedidoRequest(
            idCliente = 1,
            direccionEntrega = "Calle 10 #5-20",
            subtotal = 35000.0,
            total = 38500.0,
            detallePedido = listOf(
                DetallePedidoRequest(idProducto = 1, cantidad = 2),
                DetallePedidoRequest(idProducto = 3, cantidad = 1)
            )
        )

        assertEquals(1, request.idCliente)
        assertEquals("Calle 10 #5-20", request.direccionEntrega)
        assertEquals(35000.0, request.subtotal, 0.01)
        assertEquals(38500.0, request.total, 0.01)
        assertEquals(2, request.detallePedido.size)
    }

    @Test
    fun pedidoRequest_listaDetalleVacia_sePermite() {
        val request = PedidoRequest(
            idCliente = 1,
            direccionEntrega = "Calle Test",
            subtotal = 0.0,
            total = 0.0,
            detallePedido = emptyList()
        )

        assertTrue(request.detallePedido.isEmpty())
        assertEquals(0.0, request.total, 0.01)
    }

    @Test
    fun pedidoRequest_listaDetalleMultiplesItems() {
        val items = (1..5).map { i ->
            DetallePedidoRequest(idProducto = i, cantidad = i * 2)
        }
        val request = PedidoRequest(
            idCliente = 1,
            direccionEntrega = "Direccion",
            subtotal = 100.0,
            total = 100.0,
            detallePedido = items
        )

        assertEquals(5, request.detallePedido.size)
        assertEquals(2, request.detallePedido[0].cantidad)
        assertEquals(10, request.detallePedido[4].cantidad)
    }
}
