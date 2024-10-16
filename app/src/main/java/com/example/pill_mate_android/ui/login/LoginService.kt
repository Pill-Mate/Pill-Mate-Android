package com.example.pill_mate_android.ui.login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/api/v1/auth/signup")
    fun login(@Body login:LoginData): Call<ResponseToken>
}