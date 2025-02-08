package com.pill_mate.pill_mate_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    var userToken: String? = null

    companion object {
        private var instance: GlobalApplication? = null

        fun getInstance(): GlobalApplication? = instance
        fun getToken(): String {
            return instance?.userToken ?: ""
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}