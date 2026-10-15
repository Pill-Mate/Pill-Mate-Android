package com.pill_mate.pill_mate_android.pillstats

data class StatsDrug(
    val name: String,
    val takingDays: Int,
    val takenDays: Set<Int>
)
