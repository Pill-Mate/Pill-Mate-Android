package com.pill_mate.pill_mate_android.search.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SearchMedicineResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<SearchMedicineItem>
)

@Parcelize
data class SearchMedicineItem(
    val itemSeq: Long,
    val itemName: String,
    val className: String,
    val entpName: String,
    val itemImage: String?
) : Parcelable