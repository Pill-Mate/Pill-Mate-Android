package com.pill_mate.pill_mate_android

import android.util.Log
import com.pill_mate.pill_mate_android.login.model.FcmTokenData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FcmTokenManager {
    fun sendFcmTokenToServer(fcmToken: String) {
        val call: Call<Void> = ServiceCreator.fcmService.sendFcmTokenData(FcmTokenData(fcmToken))

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("FCM", "서버에 FCM 토큰 등록 완료")
                } else {
                    Log.e("FCM", "FCM 토큰 등록 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FCM", "FCM 토큰 등록 중 오류: ${t.message}")
            }
        })
    }
}