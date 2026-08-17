package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.Pago
import com.example.ga8_220501096_aa2_ev01.data.network.PagoService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class PagoRepository {

    private val pagoService = RetrofitClient.instance.create(PagoService::class.java)

    suspend fun crearPago(pago: Pago): Result<Pago> {
        return try {
            val resultado = pagoService.crearPago(pago)
            Result.success(resultado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPagosPorPedido(idPedido: Int): Result<List<Pago>> {
        return try {
            val pagos = pagoService.getPagosPorPedido(idPedido)
            Result.success(pagos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
