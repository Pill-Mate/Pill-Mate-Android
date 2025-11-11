package com.pill_mate.pill_mate_android.pillcheck.model

data class ResponseMedicineDetail(
    val className: String,
    val medicineImage: String,
    val entpName: String,
    val efficacy: String,
    val medicineName: String,
    val caution: String,
    val sideEffect: String,
    val storage: String,
    val userMethod: String
)
