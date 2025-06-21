package com.pill_mate.pill_mate_android.search.model

data class SearchPharmacyResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<SearchPharmacyItem>
)

data class SearchPharmacyItem(
    val address: String,
    val name: String,
    val phone: String
)