package com.example.pill_mate_android

import com.example.pill_mate_android.pillSearch.api.MedicineRegistrationService
import com.example.pill_mate_android.ui.login.LoginService
import com.example.pill_mate_android.ui.login.OnBoardingService
import com.example.pill_mate_android.ui.pillcheck.HomeService
import com.example.pill_mate_android.ui.pillcheck.WeeklyCalendarService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceCreator {
    // 서버 URL
    private const val BASE_URL = "https://ad0n0kzypg.execute-api.ap-northeast-2.amazonaws.com"

    // Gson 설정 추가
    private val gson: Gson = GsonBuilder()
        .setLenient() // 비표준 JSON 허용
        .create()

    // Retrofit 인스턴스 생성
    private val userRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson)) // 커스터마이징된 Gson 적용
        .build()

    private fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .run {
                addInterceptor(TokenInterceptor())
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                build()
            }

    // 서비스 인터페이스 초기화
    val loginService: LoginService = userRetrofit.create(LoginService::class.java)
    val onBoardingService: OnBoardingService = userRetrofit.create(OnBoardingService::class.java)
    val homeService: HomeService = userRetrofit.create(HomeService::class.java)
    val weeklyCalendarService: WeeklyCalendarService = userRetrofit.create(WeeklyCalendarService::class.java)
    val medicineRegistrationService: MedicineRegistrationService = userRetrofit.create(MedicineRegistrationService::class.java)
}