package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.Pago
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PagoService {

    @POST("api/pagos")
    suspend fun crearPago(@Body pago: Pago): Pago

    @GET("api/pagos")
    suspend fun getPagos(): List<Pago>

    @GET("api/pagos/{idPago}")
    suspend fun getPagoPorId(@Path("idPago") idPago: Int): Pago

    @GET("api/pagos/pedido/{idPedido}")
    suspend fun getPagosPorPedido(@Path("idPedido") idPedido: Int): List<Pago>
}
