package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.Repartidor
import com.example.ga8_220501096_aa2_ev01.data.network.RepartidorService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class RepartidorRepository {

    private val repartidorService = RetrofitClient.instance.create(RepartidorService::class.java)

    suspend fun getRepartidores(): Result<List<Repartidor>> {
        return try {
            val repartidores = repartidorService.getRepartidores()
            Result.success(repartidores)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRepartidorPorId(id: Int): Result<Repartidor> {
        return try {
            val repartidor = repartidorService.getRepartidorPorId(id)
            Result.success(repartidor)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
