package com.example.pill_mate_android.ui.onboarding

import java.sql.Time

data class OnBoardingData( //val email: String,
    val wakeupTime: Time, val bedTime: Time, val morningTime: Time, val lunchTime: Time, val dinnerTime: Time
)
