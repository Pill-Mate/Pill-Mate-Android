package com.pill_mate.pill_mate_android.ui.setting

import com.pill_mate.pill_mate_android.ui.login.KaKaoTokenData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface SettingService {
    @GET("/api/v1/mypage/mypagereturn")
    fun getUserInfoData(): Call<ResponseUserInfo>
}

interface SignOutService {
    @POST("/api/v1/auth/signout")
    fun sendTokenData(@Body tokenData: KaKaoTokenData): Call<Void>
}

interface LogOutService {
    @POST("/api/v1/auth/logout")
    fun logout(): Call<Void>
}

interface GetRoutineService {
    @GET("/api/v1/mypage/routinedata")
    fun getRoutineData(): Call<ResponseRoutine>
}

interface PatchRoutineService {
    @PATCH("/api/v1/mypage/routineupdate")
    fun patchRoutineData(@Body routine: RoutineData): Call<Void>
}

interface PatchAlarmMarketingService {
    @PATCH("/api/v1/mypage/alarmupdate/marketing")
    fun patchAlarmMarketingData(@Body alarmMarketing: AlarmMarketingData): Call<Void>
}

interface PatchAlarmInfoService {
    @PATCH("/api/v1/mypage/alarmupdate/information")
    fun patchAlarmInfoData(@Body alarmInfo: AlarmInfoData): Call<Void>
}