package com.pill_mate.pill_mate_android.setting.model

data class ResponseUserInfo(
    val userName: String,
    val email: String,
    val profileImage: String,
    val alarmMarketing: Boolean,
    val alarmInfo: Boolean
)
