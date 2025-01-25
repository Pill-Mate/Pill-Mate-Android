package com.example.pill_mate_android

import com.example.pill_mate_android.ui.login.LoginService
import com.example.pill_mate_android.ui.login.OnBoardingService
import com.example.pill_mate_android.ui.pillcheck.HomeService
import com.example.pill_mate_android.ui.pillcheck.MedicineCheckService
import com.example.pill_mate_android.ui.pillcheck.WeeklyCalendarService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.*

object ServiceCreator {
    //서버에서 준 URL 입력
    private const val BASE_URL = "http://192.168.0.8:8080"

    private val userRetrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create()).build()

    private fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS).run {
                addInterceptor(TokenInterceptor())
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                build()
            }

    val loginService: LoginService = userRetrofit.create(LoginService::class.java)
    val onBoardingService: OnBoardingService = userRetrofit.create(OnBoardingService::class.java)
    val homeService: HomeService = userRetrofit.create(HomeService::class.java)
    val weeklyCalendarService: WeeklyCalendarService = userRetrofit.create(WeeklyCalendarService::class.java)
    val medicineCheckService: MedicineCheckService = userRetrofit.create(MedicineCheckService::class.java)
}