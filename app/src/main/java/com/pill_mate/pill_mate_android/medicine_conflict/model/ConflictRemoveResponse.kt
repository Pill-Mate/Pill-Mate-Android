package com.pill_mate.pill_mate_android.medicine_conflict.model

data class ConflictRemoveResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)