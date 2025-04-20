package com.pill_mate.pill_mate_android.ui.pillcheck

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.fetch(
    onError: (Throwable) -> Unit = {}, onSuccess: (T) -> Unit = {},
) {
    this.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    onSuccess(body)
                } else {
                    onError(Throwable("응답은 성공했지만 body가 null입니다"))
                }
            } else {
                onError(Throwable("응답 실패: code ${response.code()}"))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            onError(t)
        }
    })
}
