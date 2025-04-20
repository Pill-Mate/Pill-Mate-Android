package com.pill_mate.pill_mate_android

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.pill_mate.pill_mate_android.ui.login.RefreshTokenData
import com.pill_mate.pill_mate_android.ui.login.ResponseError
import okhttp3.Interceptor
import okhttp3.Protocol.HTTP_1_1
import okhttp3.Request
import okhttp3.Response
import okhttp3.Response.Builder
import okhttp3.ResponseBody.Companion.toResponseBody

// 모든 API 호출에 jwtToken 추가
// API 응답이 JWTEXPIRED401이면 → RefreshToken으로 JWT 재발급 시도
// REFRESHEXPIRED401이면 → 로그아웃 + 로그인 화면 이동
class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val jwtToken = GlobalApplication.getToken()
        val requestBuilder = originalRequest.newBuilder()

        // JWT 토큰이 존재하면 Authorization 헤더에 추가
        if (jwtToken.isNotEmpty()) {
            requestBuilder.header("Authorization", "Bearer $jwtToken")
        }

        val response = chain.proceed(requestBuilder.build())

        // JWT 토큰 만료 시
        if (response.code == 401) {
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            val errorResponse = runCatching {
                Gson().fromJson(errorBody, ResponseError::class.java)
            }.getOrNull()

            when (errorResponse?.code) {
                "JWTEXPIRED401" -> {
                    response.close()

                    synchronized(refreshLock) {
                        if (!isRefreshing) { // 첫번째 요청만 Refresh 시도
                            isRefreshing = true
                            newAccessToken = refreshJwtToken(GlobalApplication.getRefreshToken())

                            newAccessToken?.let {
                                GlobalApplication.saveToken(it)
                                Log.i("토큰", "새 JWT 발급 성공: $newAccessToken")
                            }

                            isRefreshing = false
                            refreshLock.notifyAll()
                        } else { // 이미 다른 요청이 refresh 중이라면 기다림
                            while (isRefreshing) {
                                try {
                                    refreshLock.wait()
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        // 새 토큰이 있다면 재시도
                        return if (!newAccessToken.isNullOrEmpty()) {
                            val retryRequest =
                                originalRequest.newBuilder().header("Authorization", "Bearer $newAccessToken").build()
                            chain.proceed(retryRequest)
                        } else {
                            GlobalApplication.logout(context)
                            createEmptyUnauthorizedResponse(originalRequest)
                        }
                    }
                }

                "REFRESHEXPIRED401" -> {
                    response.close()
                    Log.i("토큰", "Refresh Token도 만료됨 → 로그아웃 처리")
                    GlobalApplication.logout(context)

                    return createEmptyUnauthorizedResponse(originalRequest)
                }

                "USERNOTINDB401" -> {
                    response.close()
                    Log.i("토큰", "DB에 사용자 없음 → 로그아웃 처리")
                    GlobalApplication.logout(context)

                    return createEmptyUnauthorizedResponse(originalRequest)
                }
            }
        }
        return response
    }

    // RefreshToken으로 새 JWTToken 발급
    private fun refreshJwtToken(refreshToken: String): String? {
        if (refreshToken.isEmpty()) return null

        val refreshTokenData = RefreshTokenData(refreshToken)

        return try {
            val call = ServiceCreator.refreshTokenService.reissue(refreshTokenData)
            val response = call.execute()

            if (response.isSuccessful) {
                val body = response.body()
                body?.refreshToken?.let { GlobalApplication.saveRefreshToken(it) }
                return body?.accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("토큰", "Refresh 실패: ${e.message}")
            null
        }
    }

    private fun createEmptyUnauthorizedResponse(originalRequest: Request): Response {
        return Builder().request(originalRequest).protocol(HTTP_1_1).code(401).message("Unauthorized - token expired")
            .body("".toResponseBody(null)).build()
    }

    companion object {
        @Volatile
        private var isRefreshing = false
        private val refreshLock = Object()

        // 모든 요청들이 공유할 수 있도록 최신 토큰을 저장
        @Volatile
        private var newAccessToken: String? = null
    }
}
