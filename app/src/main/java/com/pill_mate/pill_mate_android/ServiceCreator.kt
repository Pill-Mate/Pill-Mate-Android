package com.pill_mate.pill_mate_android

import com.pill_mate.pill_mate_android.medicine_registration.api.MedicineRegistrationService
import com.pill_mate.pill_mate_android.ui.login.LoginService
import com.pill_mate.pill_mate_android.ui.login.OnBoardingService
import com.pill_mate.pill_mate_android.ui.pillcheck.HomeService
import com.pill_mate.pill_mate_android.ui.pillcheck.MedicineCheckService
import com.pill_mate.pill_mate_android.ui.pillcheck.WeeklyCalendarService
import com.pill_mate.pill_mate_android.ui.setting.GetRoutineService
import com.pill_mate.pill_mate_android.ui.setting.LogOutService
import com.pill_mate.pill_mate_android.ui.setting.PatchRoutineService
import com.pill_mate.pill_mate_android.ui.setting.SettingService
import com.pill_mate.pill_mate_android.ui.setting.SignOutService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.*

object ServiceCreator { // 서버 URL
    // Gson 설정 추가
    private val gson: Gson = GsonBuilder().setLenient() // 비표준 JSON 허용
        .create()

    // Retrofit 인스턴스 생성
    private val userRetrofit: Retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson)) // 커스터마이징된 Gson 적용
        .build()

    private fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS).run {
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
    val medicineRegistrationService: MedicineRegistrationService =
        userRetrofit.create(MedicineRegistrationService::class.java)
    val medicineCheckService: MedicineCheckService = userRetrofit.create(MedicineCheckService::class.java)
    val settingService: SettingService = userRetrofit.create(SettingService::class.java)
    val signOutService: SignOutService = userRetrofit.create(SignOutService::class.java)
    val logOutService: LogOutService = userRetrofit.create(LogOutService::class.java)
    val getRoutineService: GetRoutineService = userRetrofit.create(GetRoutineService::class.java)
    val patchRoutineService: PatchRoutineService = userRetrofit.create(PatchRoutineService::class.java)
}