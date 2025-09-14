package com.pill_mate.pill_mate_android

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.kakao.sdk.common.KakaoSdk
import com.pill_mate.pill_mate_android.login.view.KakaoLoginActivity
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.plugins.SessionReplayPlugin
import com.amplitude.common.Logger

class GlobalApplication : Application() {

    companion object {
        private lateinit var instance: GlobalApplication
        private var isLoggingOut = false

        // Amplitude 인스턴스
        lateinit var amplitude: Amplitude
            private set

        fun getInstance(): GlobalApplication = instance

        private const val PREF_NAME = "secure_prefs"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        private fun getSecurePrefs(context: Context) = EncryptedSharedPreferences.create(
            PREF_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        fun getToken(): String {
            val prefs = getSecurePrefs(instance)
            return prefs.getString(KEY_JWT_TOKEN, "") ?: ""
        }

        fun getRefreshToken(): String {
            val prefs = getSecurePrefs(instance)
            return prefs.getString(KEY_REFRESH_TOKEN, "") ?: ""
        }

        fun saveToken(token: String) {
            val prefs = getSecurePrefs(instance)
            prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
        }

        fun saveRefreshToken(refreshToken: String) {
            val prefs = getSecurePrefs(instance)
            prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        }

        fun resetLogoutFlag() {
            isLoggingOut = false
        }

        fun logout(context: Context) {
            if (isLoggingOut) return // 중복 로그아웃 방지

            isLoggingOut = true

            // JWT, RefreshToken 삭제
            val prefs = getSecurePrefs(context)
            prefs.edit().clear().apply()

            // Kakao accessToken 삭제
            val kakaoPrefs = context.getSharedPreferences("kakao_prefs", Context.MODE_PRIVATE)
            kakaoPrefs.edit().clear().apply()

            val intent = Intent(context, KakaoLoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        // Amplitude 초기화
        amplitude = Amplitude(
            Configuration(
                apiKey = BuildConfig.AMPLITUDE_API_KEY,
                context = applicationContext
            )
        )
        amplitude.add(SessionReplayPlugin()) // 세션 리플레이 플러그인 추가
        amplitude.logger.logMode = Logger.LogMode.DEBUG // Debug 로그 활성화
    }
}