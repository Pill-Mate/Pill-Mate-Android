package com.pill_mate.pill_mate_android.onboarding.model

data class OnBoardingData(
    var alarmMarketing: Boolean?,
    var alarmInfo: Boolean?,
    val wakeupTime: String?,
    val bedTime: String?,
    val morningTime: String?,
    val lunchTime: String?,
    val dinnerTime: String?
)
