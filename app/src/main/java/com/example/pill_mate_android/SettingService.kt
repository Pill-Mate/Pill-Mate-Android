package com.example.pill_mate_android

import retrofit2.Call
import retrofit2.http.GET

interface SettingService {
    @GET("/api/v1/mypage/userinforeturn")
    fun getUserInfoData(): Call<ResponseUserInfo>
}