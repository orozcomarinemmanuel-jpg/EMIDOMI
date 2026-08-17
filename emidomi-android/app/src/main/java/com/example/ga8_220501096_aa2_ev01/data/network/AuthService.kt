package com.example.ga8_220501096_aa2_ev01.data.network

import com.example.ga8_220501096_aa2_ev01.data.model.Cliente
import com.example.ga8_220501096_aa2_ev01.data.model.LoginRequest
import com.example.ga8_220501096_aa2_ev01.data.model.RegistroRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): String

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Cliente
}
