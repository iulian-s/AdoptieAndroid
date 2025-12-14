package com.example.adoptie

import com.example.adoptie.anunt.AnimaluteApiService
import com.example.adoptie.anunt.AnunturiApiService
import com.example.adoptie.localitate.LocalitateApiService
import com.example.adoptie.utilizator.UtilizatorApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://10.0.2.2:8080"
object RetrofitClient {
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl("$BASE_URL/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val anuntService: AnunturiApiService by lazy{
        retrofit.create(AnunturiApiService::class.java)
    }
    val utilizatorService: UtilizatorApiService by lazy {
        retrofit.create(UtilizatorApiService::class.java)
    }

    val localitateService: LocalitateApiService by lazy {
        retrofit.create(LocalitateApiService::class.java)
    }

    val animaluteService: AnimaluteApiService by lazy {
        retrofit.create(AnimaluteApiService::class.java)
    }
}