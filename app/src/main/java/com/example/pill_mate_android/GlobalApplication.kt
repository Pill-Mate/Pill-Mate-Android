package com.example.pill_mate_android

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
        KakaoSdk.init(this, "43540f4573a619d2d0c52342367ed58c")
    }
}