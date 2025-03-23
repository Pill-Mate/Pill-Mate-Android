package com.pill_mate.pill_mate_android.ui.pilledit

data class ResponseInActiveMedicine(
    val isSuccess: Boolean, val code: String, val message: String, val result: List<MedicineItemData>
)
