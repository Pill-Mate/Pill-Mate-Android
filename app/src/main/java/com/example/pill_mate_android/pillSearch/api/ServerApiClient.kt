package com.example.pill_mate_android.pillSearch.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerApiClient {
    private const val BASE_URL = "http://13.125.152.44:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val serverApiService: ServerApiService by lazy {
        retrofit.create(ServerApiService::class.java)
    }
}

