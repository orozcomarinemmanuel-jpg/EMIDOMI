package com.example.ga8_220501096_aa2_ev01.data.model

data class Pedido(
    val idPedido: Int?,
    val idCliente: Int?,
    val idRestaurante: Int?,
    val idRepartidor: Int?,
    val direccionEntrega: String?,
    val ciudadEntrega: String?,
    val estadoPedido: String?,
    val subtotal: Double?,
    val costoDomicilio: Double?,
    val total: Double?,
    val notas: String?,
    val fechaPedido: String?,
    val fechaEntrega: String?,
    val detallePedido: List<DetallePedido>?
)

data class DetallePedido(
    val idDetalle: Int?,
    val idPedido: Int?,
    val idProducto: Int?,
    val cantidad: Int?,
    val precioUnitario: Double?,
    val subtotal: Double?
)

data class PedidoRequest(
    val idCliente: Int?,
    val direccionEntrega: String,
    val subtotal: Double,
    val total: Double,
    val detallePedido: List<DetallePedidoRequest>
)

data class DetallePedidoRequest(
    val idProducto: Int,
    val cantidad: Int
)
