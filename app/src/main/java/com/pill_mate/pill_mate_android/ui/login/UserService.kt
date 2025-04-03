package com.pill_mate.pill_mate_android.ui.login

import com.pill_mate.pill_mate_android.ui.onboarding.OnBoardingData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/api/v1/auth/signup")
    fun login(@Body login: KaKaoTokenData): Call<ResponseToken>
}

interface OnBoardingService {
    @POST("/api/v1/auth/onboarding")
    fun sendOnBoardingData(@Body onBoardingData: OnBoardingData): Call<Void>
}

interface RefreshTokenService {
    @POST("/api/v1/auth/reissue")
    fun reissue(@Body refreshTokenData: RefreshTokenData): Call<ResponseRefreshToken>
}
