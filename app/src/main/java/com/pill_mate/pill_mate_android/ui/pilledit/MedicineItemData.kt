package com.pill_mate.pill_mate_android.ui.pilledit

data class MedicineItemData(
    val startDate: String,
    val endDate: String,
    val intakePeriod: Int,
    val className: String,
    val medicineName: String,
    val entpName: String,
    val image: String,
    val scheduleId: Long,
)
