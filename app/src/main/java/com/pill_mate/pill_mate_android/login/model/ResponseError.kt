package com.pill_mate.pill_mate_android.login.model

data class ResponseError(
    val isSuccess: Boolean, val code: String, val message: String
)