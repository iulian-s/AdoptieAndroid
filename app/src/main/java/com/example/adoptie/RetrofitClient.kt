package com.example.adoptie

import android.content.Context
import com.example.adoptie.anunt.AnimaluteApiService
import com.example.adoptie.anunt.AnunturiApiService
import com.example.adoptie.auth.AuthApiService
import com.example.adoptie.auth.TokenManager
import com.example.adoptie.localitate.LocalitateApiService
import com.example.adoptie.utilizator.UtilizatorApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val BASE_IMAGE_URL = BuildConfig.PIC_URL
const val BASE_URL = BuildConfig.BACKEND_URL
object RetrofitClient {
    private var tokenManager: TokenManager? = null

    fun init(context: Context) {
        tokenManager = TokenManager(context)
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        tokenManager?.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if(response.code == 401){
            tokenManager?.deleteToken()
            runBlocking { AuthEvents.triggerLogout() }
        }

        response



    }
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
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

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }


}