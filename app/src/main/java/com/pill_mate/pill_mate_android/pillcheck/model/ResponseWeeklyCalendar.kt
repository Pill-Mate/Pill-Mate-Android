package com.pill_mate.pill_mate_android.pillcheck.model

data class ResponseWeeklyCalendar(
    override val sunday: Boolean,
    override val monday: Boolean,
    override val tuesday: Boolean,
    override val wednesday: Boolean,
    override val thursday: Boolean,
    override val friday: Boolean,
    override val saturday: Boolean,
) : WeeklyIconsData
