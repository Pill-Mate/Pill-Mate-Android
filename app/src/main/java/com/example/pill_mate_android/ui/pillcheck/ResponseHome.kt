package com.example.pill_mate_android.ui.pillcheck

data class ResponseHome(
    val sunday: Boolean,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val countAll: Int,
    val countLeft: Int,
    val medicineList: List<Data>
) {
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
