package com.example.adoptie.anunt

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnunturiApiService {
    @GET("/api/anunturi/active")
    suspend fun getAnunturiActive(): List<AnuntDTO>

    @GET("api/anunturi/{id}")
    suspend fun getAnuntDetails(@Path("id") id: Long): AnuntDTO

    @GET("api/anunturi/razaLocalitate")
    suspend fun getAnunturiInRaza(
        @Query("localitateId") localitateId: Long,
        @Query("razaKm") razaKm: Double
    ): List<AnuntDTO>

    @GET("api/anunturi/judet/{numeJudet}")
    suspend fun getAnunturiByJudet(@Path("numeJudet") numeJudet: String): List<AnuntDTO>
}