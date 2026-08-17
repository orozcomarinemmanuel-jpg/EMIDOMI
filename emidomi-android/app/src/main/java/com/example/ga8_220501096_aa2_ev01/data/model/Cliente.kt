package com.example.ga8_220501096_aa2_ev01.data.model

data class Cliente(
    val idCliente: Int?,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val estado: Boolean?,
    val fechaRegistro: String?
)
