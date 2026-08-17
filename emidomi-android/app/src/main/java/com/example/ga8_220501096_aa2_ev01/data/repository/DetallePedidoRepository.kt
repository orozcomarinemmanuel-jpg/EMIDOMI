package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.DetallePedido
import com.example.ga8_220501096_aa2_ev01.data.network.DetallePedidoService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class DetallePedidoRepository {

    private val detalleService = RetrofitClient.instance.create(DetallePedidoService::class.java)

    suspend fun getDetallesPorPedido(idPedido: Int): Result<List<DetallePedido>> {
        return try {
            val detalles = detalleService.getDetallesPorPedido(idPedido)
            Result.success(detalles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
