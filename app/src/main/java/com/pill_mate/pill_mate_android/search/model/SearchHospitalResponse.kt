package com.pill_mate.pill_mate_android.search.model

import com.google.gson.annotations.SerializedName

data class SearchHospitalResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<SearchHospitalItem>
)

data class SearchHospitalItem(
    @SerializedName("dutyAddr")
    val address: String,

    @SerializedName("dutyName")
    val name: String,

    @SerializedName("dutyTel")
    val phone: String
)