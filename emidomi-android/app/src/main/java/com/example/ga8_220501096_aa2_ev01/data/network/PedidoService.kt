package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.Pedido
import com.example.ga8_220501096_aa2_ev01.data.model.PedidoRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PedidoService {

    @POST("api/pedidos")
    suspend fun crearPedido(@Body request: PedidoRequest): Pedido

    @GET("api/pedidos/cliente/{idCliente}")
    suspend fun getPedidosPorCliente(@Path("idCliente") idCliente: Int): List<Pedido>

    @GET("api/pedidos/{idPedido}")
    suspend fun getPedidoPorId(@Path("idPedido") idPedido: Int): Pedido
}
