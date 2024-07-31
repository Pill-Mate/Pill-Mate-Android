package com.example.pill_mate_android.pillSearch.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.ResponseBody
import android.util.Log
import com.example.pill_mate_android.BuildConfig

object PillApiClient {
    private const val BASE_URL = "https://apis.data.go.kr/1471000/"

    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val responseLoggingInterceptor = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val responseBody = response.body
            val responseBodyString = responseBody?.string()

            Log.d("API_RESPONSE", "Response: $responseBodyString")

            response.newBuilder()
                .body(ResponseBody.create(responseBody?.contentType(), responseBodyString ?: ""))
                .build()
        }

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(responseLoggingInterceptor)
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient() // Lenient 모드 설정
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson)) // 커스텀 Gson 추가
            .build()
    }

    val pillApiService: PillApiService by lazy {
        retrofit.create(PillApiService::class.java)
    }
}