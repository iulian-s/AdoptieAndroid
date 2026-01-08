package com.example.adoptie.utilizator

import com.example.adoptie.anunt.AnuntDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UtilizatorApiService {
    @GET("api/utilizator/{id}")
    suspend fun getUtilizatorDetails(@Path("id") id: Long): UtilizatorDTO

    @GET("api/utilizator/eu")
    suspend fun getInfoUtilizator(): Response<UtilizatorDTO>

    @GET("api/anunturi/eu/anunturi")
    suspend fun getAnunturiProprii(): Response<List<AnuntDTO>>

}