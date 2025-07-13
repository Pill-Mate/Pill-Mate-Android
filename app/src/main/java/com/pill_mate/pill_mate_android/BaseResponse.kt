package com.pill_mate.pill_mate_android

data class BaseResponse<T>(
    val isSuccess: Boolean, val code: String, val message: String, val result: T
)