package com.example.adoptie.anunt

import retrofit2.http.GET
import retrofit2.http.Path

interface AnunturiApiService {
    @GET("/api/anunturi/active")
    suspend fun getAnunturiActive(): List<AnuntDTO>

    @GET("api/anunturi/{id}")
    suspend fun getAnuntDetails(@Path("id") id: Long): AnuntDTO
}