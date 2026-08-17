package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.Repartidor
import retrofit2.http.GET
import retrofit2.http.Path

interface RepartidorService {

    @GET("api/repartidores")
    suspend fun getRepartidores(): List<Repartidor>

    @GET("api/repartidores/{id}")
    suspend fun getRepartidorPorId(@Path("id") id: Int): Repartidor
}
