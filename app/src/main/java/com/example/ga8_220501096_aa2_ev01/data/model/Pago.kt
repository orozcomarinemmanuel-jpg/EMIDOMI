package com.example.ga8_220501096_aa2_ev01.data.model

data class Pago(
    val idPago: Int?,
    val idPedido: Int?,
    val metodoPago: String?,
    val estadoPago: String?,
    val monto: Double,
    val referencia: String?,
    val fechaPago: String?
)
