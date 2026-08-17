package com.example.ga8_220501096_aa2_ev01.data.repository

import com.example.ga8_220501096_aa2_ev01.data.model.Cliente
import com.example.ga8_220501096_aa2_ev01.data.model.LoginRequest
import com.example.ga8_220501096_aa2_ev01.data.model.RegistroRequest
import com.example.ga8_220501096_aa2_ev01.data.network.AuthService
import com.example.ga8_220501096_aa2_ev01.data.network.RetrofitClient

class AuthRepository {

    private val authService = RetrofitClient.instance.create(AuthService::class.java)

    suspend fun login(correo: String, contrasena: String): Result<String> {
        return try {
            val respuesta = authService.login(LoginRequest(correo, contrasena))
            Result.success(respuesta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registro(request: RegistroRequest): Result<Cliente> {
        return try {
            val cliente = authService.registro(request)
            Result.success(cliente)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
