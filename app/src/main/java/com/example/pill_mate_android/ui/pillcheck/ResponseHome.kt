package com.example.pill_mate_android.ui.pillcheck

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
        val medicineName: String,
        val medicineImage: String
    )
}

data class GroupedMedicine( // "MORNING", "AFTERNOON", "EVENING"
    val intakeCount: String, val times: List<TimeGroup>
)

data class TimeGroup( // 예: "08:00:00"
    val intakeTime: String, val medicines: List<ResponseHome.Data>
)
