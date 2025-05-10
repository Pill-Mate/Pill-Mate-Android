package com.pill_mate.pill_mate_android.login.model

data class ResponseToken(
    val jwtToken: String?, val refreshToken: String, val login: Boolean?
)
