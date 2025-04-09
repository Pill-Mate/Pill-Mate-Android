package com.pill_mate.pill_mate_android.ui.login

data class ResponseToken(
    val jwtToken: String?, val refreshToken: String, val login: Boolean?
)
