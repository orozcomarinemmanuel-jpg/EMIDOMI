package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.Restaurante
import com.example.ga8_220501096_aa2_ev01.data.network.RestauranteService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class RestauranteRepository {

    private val restauranteService = RetrofitClient.instance.create(RestauranteService::class.java)

    suspend fun getRestaurantes(): Result<List<Restaurante>> {
        return try {
            val restaurantes = restauranteService.getRestaurantes()
            Result.success(restaurantes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRestaurantePorId(id: Int): Result<Restaurante> {
        return try {
            val restaurante = restauranteService.getRestaurantePorId(id)
            Result.success(restaurante)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
