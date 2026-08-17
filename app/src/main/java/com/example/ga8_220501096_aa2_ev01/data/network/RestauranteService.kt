package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.Restaurante
import retrofit2.http.GET
import retrofit2.http.Path

interface RestauranteService {

    @GET("api/restaurantes")
    suspend fun getRestaurantes(): List<Restaurante>

    @GET("api/restaurantes/{id}")
    suspend fun getRestaurantePorId(@Path("id") id: Int): Restaurante
}
