package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.Pedido
import com.example.ga8_220501096_aa2_ev01.data.model.PedidoRequest
import com.example.ga8_220501096_aa2_ev01.data.network.PedidoService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class PedidoRepository {

    private val pedidoService = RetrofitClient.instance.create(
        PedidoService::class.java
    )

    suspend fun crearPedido(request: PedidoRequest): Result<Pedido> {
        return try {
            val pedido = pedidoService.crearPedido(request)
            Result.success(pedido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPedidosPorCliente(idCliente: Int): Result<List<Pedido>> {
        return try {
            val pedidos = pedidoService.getPedidosPorCliente(idCliente)
            Result.success(pedidos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPedidoPorId(idPedido: Int): Result<Pedido> {
        return try {
            val pedido = pedidoService.getPedidoPorId(idPedido)
            Result.success(pedido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
