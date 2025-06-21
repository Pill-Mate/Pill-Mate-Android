package com.pill_mate.pill_mate_android.search.model

data class SearchHospitalResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<SearchHospitalItem>
)

data class SearchHospitalItem(
    val address: String,
    val name: String,
    val phone: String
)