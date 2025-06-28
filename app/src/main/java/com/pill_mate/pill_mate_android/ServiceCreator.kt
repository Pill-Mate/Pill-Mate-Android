package com.pill_mate.pill_mate_android

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pill_mate.pill_mate_android.login.model.FcmService
import com.pill_mate.pill_mate_android.login.model.LoginService
import com.pill_mate.pill_mate_android.login.model.OnBoardingService
import com.pill_mate.pill_mate_android.login.model.RefreshTokenService
import com.pill_mate.pill_mate_android.medicine_registration.api.MedicineRegistrationService
import com.pill_mate.pill_mate_android.notice.NotificationService
import com.pill_mate.pill_mate_android.pillcheck.model.HomeService
import com.pill_mate.pill_mate_android.pillcheck.model.MedicineCheckService
import com.pill_mate.pill_mate_android.pillcheck.model.WeeklyCalendarService
import com.pill_mate.pill_mate_android.pilledit.api.MedicineEditService
import com.pill_mate.pill_mate_android.pilledit.model.ActiveMedicineService
import com.pill_mate.pill_mate_android.pilledit.model.InActiveMedicineService
import com.pill_mate.pill_mate_android.pilledit.model.StopMedicineService
import com.pill_mate.pill_mate_android.search.api.SearchService
import com.pill_mate.pill_mate_android.setting.model.GetRoutineService
import com.pill_mate.pill_mate_android.setting.model.PatchAlarmInfoService
import com.pill_mate.pill_mate_android.setting.model.PatchAlarmMarketingService
import com.pill_mate.pill_mate_android.setting.model.PatchRoutineService
import com.pill_mate.pill_mate_android.setting.model.SettingService
import com.pill_mate.pill_mate_android.setting.model.SignOutService
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
    private val userRetrofit: Retrofit =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(provideOkHttpClient(GlobalApplication.getInstance()!!))
            .addConverterFactory(GsonConverterFactory.create(gson)) // 커스터마이징된 Gson 적용
            .build()

    private fun provideOkHttpClient(context: Context): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS).run {
                addInterceptor(TokenInterceptor(context))
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                build()
            }

    // 서비스 인터페이스 초기화
    val loginService: LoginService = userRetrofit.create(LoginService::class.java)
    val refreshTokenService: RefreshTokenService = userRetrofit.create(RefreshTokenService::class.java)
    val onBoardingService: OnBoardingService = userRetrofit.create(OnBoardingService::class.java)
    val homeService: HomeService = userRetrofit.create(HomeService::class.java)
    val weeklyCalendarService: WeeklyCalendarService = userRetrofit.create(WeeklyCalendarService::class.java)
    val searchService: SearchService = userRetrofit.create(SearchService::class.java)
    val medicineRegistrationService: MedicineRegistrationService =
        userRetrofit.create(MedicineRegistrationService::class.java)
    val medicineCheckService: MedicineCheckService = userRetrofit.create(MedicineCheckService::class.java)
    val settingService: SettingService = userRetrofit.create(SettingService::class.java)
    val signOutService: SignOutService = userRetrofit.create(SignOutService::class.java)
    val getRoutineService: GetRoutineService = userRetrofit.create(GetRoutineService::class.java)
    val patchRoutineService: PatchRoutineService = userRetrofit.create(PatchRoutineService::class.java)
    val patchAlarmMarketingService: PatchAlarmMarketingService =
        userRetrofit.create(PatchAlarmMarketingService::class.java)
    val patchAlarmInfoService: PatchAlarmInfoService = userRetrofit.create(PatchAlarmInfoService::class.java)
    val activeMedicineService: ActiveMedicineService = userRetrofit.create(ActiveMedicineService::class.java)
    val inActiveMedicineService: InActiveMedicineService = userRetrofit.create(InActiveMedicineService::class.java)
    val stopMedicineService: StopMedicineService = userRetrofit.create(StopMedicineService::class.java)
    val medicineEditService: MedicineEditService = userRetrofit.create(MedicineEditService::class.java)
    val notificationService: NotificationService = userRetrofit.create(NotificationService::class.java)
    val fcmService: FcmService = userRetrofit.create(FcmService::class.java)
}