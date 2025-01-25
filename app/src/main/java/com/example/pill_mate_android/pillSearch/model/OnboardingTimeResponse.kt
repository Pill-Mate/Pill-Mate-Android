package com.example.pill_mate_android.pillSearch.model

data class OnboardingTimeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: OnboardingTimes
)

data class OnboardingTimes(
    val morningTime: String,
    val lunchTime: String,
    val dinnerTime: String,
    val wakeupTime: String,
    val bedTime: String,
    val alarmMarketing: Boolean
)