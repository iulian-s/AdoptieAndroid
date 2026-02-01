package com.example.adoptie.utilizator

import com.example.adoptie.anunt.AnuntDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UtilizatorApiService {
    @GET("api/utilizator/{id}")
    suspend fun getUtilizatorDetails(@Path("id") id: Long): UtilizatorDTO

    @GET("api/utilizator/eu")
    suspend fun getInfoUtilizator(): Response<UtilizatorDTO>

    @GET("api/anunturi/eu/anunturi")
    suspend fun getAnunturiProprii(): Response<List<AnuntDTO>>

    @Multipart
    @PUT("api/utilizator/edit")
    suspend fun editareProfil(
        @Part("dto") dto: RequestBody,
        @Part avatar: MultipartBody.Part?
    ): Response<UtilizatorDTO>

    @DELETE("/api/utilizator")
    suspend fun stergereContUtilizator(
        @Query("parola") parola: String
    ): Response<Void>
}