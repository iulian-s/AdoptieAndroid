package com.example.adoptie.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    data class AuthRequest(val username: String, val parola: String)
    data class AuthResponse(val token: String)
    data class CreareUtilizatorDTO(
        val username: String,     // Nickname-ul (ex: "ionut99")
        val email: String,        // Adresa de email (ex: "ion@gmail.com")
        val parola: String,
        val nume: String,         // Numele real
        val telefon: String,
        val rol: String = "USER",
        var localitateId: Long? = null
    )


    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body dto: CreareUtilizatorDTO): Response<AuthResponse>
}