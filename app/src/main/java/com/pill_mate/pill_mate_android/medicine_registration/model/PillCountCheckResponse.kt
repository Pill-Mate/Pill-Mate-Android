package com.pill_mate.pill_mate_android.medicine_registration.model

data class PillCountCheckResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)