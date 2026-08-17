package com.example.ga8_220501096_aa2_ev01

import com.example.ga8_220501096_aa2_ev01.data.model.*
import org.junit.Assert.*
import org.junit.Test

class PagoModelTest {

    @Test
    fun pago_creacionConTodosLosCampos() {
        val pago = Pago(
            idPago = 1,
            idPedido = 10,
            metodoPago = "efectivo",
            estadoPago = "pendiente",
            monto = 50000.0,
            referencia = null,
            fechaPago = "2026-08-17"
        )

        assertEquals(1, pago.idPago)
        assertEquals(10, pago.idPedido)
        assertEquals("efectivo", pago.metodoPago)
        assertEquals("pendiente", pago.estadoPago)
        assertEquals(50000.0, pago.monto, 0.01)
        assertNull(pago.referencia)
        assertEquals("2026-08-17", pago.fechaPago)
    }

    @Test
    fun pago_metodosDePagoValidos() {
        val metodosValidos = listOf(
            "efectivo", "tarjeta_credito", "tarjeta_debito",
            "transferencia", "nequi", "daviplata"
        )

        metodosValidos.forEach { metodo ->
            val pago = Pago(
                idPago = null,
                idPedido = 1,
                metodoPago = metodo,
                estadoPago = "pendiente",
                monto = 10000.0,
                referencia = null,
                fechaPago = null
            )
            assertEquals(metodo, pago.metodoPago)
        }
    }

    @Test
    fun pago_estadosValidos() {
        val estadosValidos = listOf("pendiente", "aprobado", "rechazado", "reembolsado")

        estadosValidos.forEach { estado ->
            val pago = Pago(
                idPago = null,
                idPedido = 1,
                metodoPago = "efectivo",
                estadoPago = estado,
                monto = 10000.0,
                referencia = null,
                fechaPago = null
            )
            assertEquals(estado, pago.estadoPago)
        }
    }

    @Test
    fun pago_montoCero_esValido() {
        val pago = Pago(
            idPago = null,
            idPedido = 1,
            metodoPago = "efectivo",
            estadoPago = "pendiente",
            monto = 0.0,
            referencia = null,
            fechaPago = null
        )
        assertEquals(0.0, pago.monto, 0.01)
    }

    @Test
    fun pago_conReferencia() {
        val pago = Pago(
            idPago = null,
            idPedido = 1,
            metodoPago = "transferencia",
            estadoPago = "aprobado",
            monto = 25000.0,
            referencia = "REF-12345",
            fechaPago = "2026-08-17 10:30:00"
        )
        assertEquals("REF-12345", pago.referencia)
    }
}
