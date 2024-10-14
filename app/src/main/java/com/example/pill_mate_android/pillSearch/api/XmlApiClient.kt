package com.example.pill_mate_android.pillSearch.api

import com.example.pill_mate_android.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import java.util.concurrent.TimeUnit

object XmlApiClient {
    private const val BASE_URL = "https://apis.data.go.kr/"

    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val tikXml = TikXml.Builder()
        .exceptionOnUnreadXml(false) // XML 파싱 중 예외 처리 설정
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(TikXmlConverterFactory.create(tikXml)) // XML 전용
            .build()
    }

    val xmlApiService: XmlApiService by lazy {
        retrofit.create(XmlApiService::class.java)
    }
}