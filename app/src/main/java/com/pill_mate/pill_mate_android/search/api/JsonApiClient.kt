package com.pill_mate.pill_mate_android.search.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import android.util.Log
import com.pill_mate.pill_mate_android.BuildConfig

object JsonApiClient {
    private const val BASE_URL = "https://apis.data.go.kr/"

    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        // 수정된 responseLoggingInterceptor
        val responseLoggingInterceptor = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val responseBody = response.body

            // 응답 본문을 두 번 소비하지 않도록, clone하여 로그 기록 후 사용
            val source = responseBody?.source()
            source?.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source?.buffer?.clone() // Clone the buffer for logging

            val responseBodyString = buffer?.readUtf8()
            Log.d("API_RESPONSE", "Response: $responseBodyString")

            response
        }

        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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
            .addConverterFactory(GsonConverterFactory.create(gson)) // json
            .build()
    }

    val jsonApiService: JsonApiService by lazy {
        retrofit.create(JsonApiService::class.java)
    }
}