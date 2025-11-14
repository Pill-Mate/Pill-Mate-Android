package com.pill_mate.pill_mate_android.pillcheck.model

import com.pill_mate.pill_mate_android.pillcheck.model.ResponseHome.Data

data class ResponseHome(
    override val sunday: Boolean,
    override val monday: Boolean,
    override val tuesday: Boolean,
    override val wednesday: Boolean,
    override val thursday: Boolean,
    override val friday: Boolean,
    override val saturday: Boolean,
    val countAll: Int,
    val countLeft: Int,
    val notificationRead: Boolean,
    val medicineList: List<Data>
) : WeeklyIconsData {
    data class Data(
        val medicineScheduleId: Long,
        val intakeCount: String,
        val intakeTime: String,
        val eatCount: Int,
        val eatUnit: String,
        val mealTime: Int,
        val mealUnit: String,
        val eatCheck: Boolean,
        val itemSeq: Long,
        val medicineName: String,
        val medicineImage: String
    )
}

data class GroupedMedicine( // "MORNING", "AFTERNOON", "EVENING"
    val intakeCount: String, val times: List<TimeGroup>
) {
    val isAllChecked: Boolean
        get() = times.all { timeGroup -> timeGroup.medicines.all { it.eatCheck } }
}

data class TimeGroup( // 예: "08:00:00"
    val intakeTime: String, val medicines: List<Data>
)
