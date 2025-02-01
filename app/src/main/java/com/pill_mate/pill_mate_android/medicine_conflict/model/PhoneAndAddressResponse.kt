package com.pill_mate.pill_mate_android.medicine_conflict.model

data class PhoneAndAddressResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PharmacyAndHospital
)

data class PharmacyAndHospital(
    val pharmacyName: String,
    val pharmacyAddress: String,
    val pharmacyPhoneNumber: String,
    val hospitalName: String,
    val hospitalAddress: String,
    val hospitalPhoneNumber: String
)