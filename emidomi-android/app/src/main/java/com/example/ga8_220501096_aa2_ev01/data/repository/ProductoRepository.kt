package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.Producto
import com.example.ga8_220501096_aa2_ev01.data.network.ProductoService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class ProductoRepository {

    private val productoService = RetrofitClient.instance.create(
        ProductoService::class.java
    )

    suspend fun getProductos(): Result<List<Producto>> {
        return try {
            val productos = productoService.getProductos()
            Result.success(productos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductosDisponibles(): Result<List<Producto>> {
        return try {
            val productos = productoService.getProductosDisponibles()
            Result.success(productos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
