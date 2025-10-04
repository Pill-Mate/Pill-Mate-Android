package com.pill_mate.pill_mate_android.util

import com.pill_mate.pill_mate_android.BaseResponse

inline fun <T> BaseResponse<T>.onSuccess(action: (T) -> Unit): BaseResponse<T> {
    if (isSuccess) action(result)
    return this
}

inline fun <T> BaseResponse<T>.onFailure(action: (code: String, message: String) -> Unit): BaseResponse<T> {
    if (!isSuccess) action(code, message)
    return this
}