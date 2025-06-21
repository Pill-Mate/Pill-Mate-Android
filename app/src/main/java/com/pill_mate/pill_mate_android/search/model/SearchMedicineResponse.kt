package com.pill_mate.pill_mate_android.search.model

data class SearchMedicineResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<SearchMedicineItem>
)

data class SearchMedicineItem(
    val itemSeq: Long,
    val itemName: String,
    val className: String,
    val companyName: String,      
    val itemImage: String?
)