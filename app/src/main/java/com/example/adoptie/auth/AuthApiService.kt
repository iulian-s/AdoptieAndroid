package com.example.adoptie.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    data class AuthRequest(val username: String, val parola: String)
    data class AuthResponse(val token: String)

    data class ForgotPasswordRequestDTO(
        val email: String
    )

    data class ResetPasswordRequestDTO(
        val token: String,
        val newPassword: String
    )

    data class CreareUtilizatorDTO(
        val username: String,
        val email: String,
        val parola: String,
        val nume: String,
        val telefon: String,
        val rol: String = "USER",
        var localitateId: Long? = null
    )


    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body dto: CreareUtilizatorDTO): Response<AuthResponse>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDTO): Response<Void>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDTO): Response<Void>
}