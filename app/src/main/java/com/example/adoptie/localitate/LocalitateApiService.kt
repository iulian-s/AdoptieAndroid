package com.example.adoptie.localitate

import retrofit2.http.GET
import retrofit2.http.Path

interface LocalitateApiService {
    @GET("api/localitati/{id}")
    suspend fun getLocalitateDetails(@Path("id") id: Long): LocalitateDTO
}