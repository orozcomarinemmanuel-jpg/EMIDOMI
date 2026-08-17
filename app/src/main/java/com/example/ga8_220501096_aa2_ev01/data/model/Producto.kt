package com.example.ga8_220501096_aa2_ev01.data.model

data class Producto(
    val idProducto: Int?,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagen: String?,
    val emoji: String?,
    val categoria: String?,
    val estado: Boolean?
)
