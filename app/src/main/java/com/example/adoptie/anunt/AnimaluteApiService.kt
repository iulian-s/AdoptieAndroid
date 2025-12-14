package com.example.adoptie.anunt

import retrofit2.http.GET

interface AnimaluteApiService {
    @GET("api/animalute")
    suspend fun getRase(): Map<String, List<String>>
}