package com.example.adoptie.utilizator

import retrofit2.http.GET
import retrofit2.http.Path

interface UtilizatorApiService {
    @GET("api/utilizator/{id}")
    suspend fun getUtilizatorDetails(@Path("id") id: Long): UtilizatorDTO
}