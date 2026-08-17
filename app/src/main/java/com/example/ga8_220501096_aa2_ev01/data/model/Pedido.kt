package com.example.ga8_220501096_aa2_ev01.data.model

data class Pedido(
    val idPedido: Int?,
    val idCliente: Int?,
    val fecha: String?,
    val total: Double,
    val estado: String?,
    val direccionEntrega: String,
    val detallePedido: List<DetallePedido>?
)

data class DetallePedido(
    val idDetalle: Int?,
    val idProducto: Int,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

data class PedidoRequest(
    val idCliente: Int?,
    val direccionEntrega: String,
    val detallePedido: List<DetallePedidoRequest>
)

data class DetallePedidoRequest(
    val idProducto: Int,
    val cantidad: Int
)
