package com.example.pill_mate_android

import com.example.pill_mate_android.ui.login.KaKaoTokenData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface SettingService {
    @GET("/api/v1/mypage/userinforeturn")
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