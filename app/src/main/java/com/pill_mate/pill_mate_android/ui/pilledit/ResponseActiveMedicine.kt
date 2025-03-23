package com.pill_mate.pill_mate_android.ui.pilledit

data class ResponseActiveMedicine(
    val isSuccess: Boolean, val code: String, val message: String, val result: ActiveMedicineResult
)

data class ActiveMedicineResult(
    val pillCount: Int, val currentPillResponseList: List<MedicineItemData>
)
