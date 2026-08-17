package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.Producto
import retrofit2.http.GET

interface ProductoService {

    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    @GET("api/productos/disponibles")
    suspend fun getProductosDisponibles(): List<Producto>
}
