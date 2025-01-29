package com.pill_mate.pill_mate_android

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val jwtToken = GlobalApplication.getToken()

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // JWT 토큰이 존재하면 Authorization 헤더에 추가
        if (jwtToken.isNotEmpty()) {
            requestBuilder.header("Authorization", "Bearer $jwtToken")
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)

    }
}