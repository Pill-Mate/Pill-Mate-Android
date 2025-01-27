package com.example.pill_mate_android.pillSearch.model

data class PharmacyAndHospitalResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PharmacyAndHospital
)

data class PharmacyAndHospital(
    val pharmacyName: String,
    val pharmacyAddress: String,
    val pharmacyPhone: String,
    val hospitalName: String,
    val hospitalAddress: String,
    val hospitalPhone: String
)