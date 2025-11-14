package com.pill_mate.pill_mate_android.pillcheck.util

import android.util.Log
import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.util.onFailure
import com.pill_mate.pill_mate_android.util.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

inline fun <T> Call<BaseResponse<T>>.fetch(crossinline onSuccess: (T) -> Unit) {
    enqueue(object : Callback<BaseResponse<T>> {
        override fun onResponse(call: Call<BaseResponse<T>>, response: Response<BaseResponse<T>>) {
            response.body()?.onSuccess { onSuccess(it) }?.onFailure { code, message ->
                Log.e("API 실패", "code: $code, message: $message")
            }
        }

        override fun onFailure(call: Call<BaseResponse<T>>, t: Throwable) {
            Log.e("네트워크 오류", "${t.message}")
        }
    })
}