package com.example.adoptie.localitate

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocalitateApiService {

    @GET("api/localitati")
    suspend fun getAllLocalitati(): List<LocalitateDTO>

    @GET("api/localitati/{id}")
    suspend fun getLocalitateDetails(@Path("id") id: Long): LocalitateDTO

    @GET("api/localitati/judete")
    suspend fun getJudete(): List<String>

    @GET("api/localitati/by-judet")
    suspend fun getByJudet(@Query("judet") judet: String): List<LocalitateDTO>


}