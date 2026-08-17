package com.example.ga8_220501096_aa2_ev01.data.model

data class Restaurante(
    val idRestaurante: Int?,
    val nombre: String,
    val direccion: String?,
    val ciudad: String?,
    val telefono: String?,
    val correo: String?,
    val categoria: String?,
    val calificacion: Double?,
    val estado: Boolean?,
    val fechaRegistro: String?
)
