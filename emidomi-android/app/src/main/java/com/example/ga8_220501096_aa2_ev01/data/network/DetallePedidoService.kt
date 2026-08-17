package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.DetallePedido
import retrofit2.http.GET
import retrofit2.http.Path

interface DetallePedidoService {

    @GET("api/detalle-pedido/pedido/{idPedido}")
    suspend fun getDetallesPorPedido(@Path("idPedido") idPedido: Int): List<DetallePedido>

    @GET("api/detalle-pedido/{id}")
    suspend fun getDetallePorId(@Path("id") id: Int): DetallePedido
}
