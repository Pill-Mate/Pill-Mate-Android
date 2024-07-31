package com.example.pill_mate_android.pillSearch.model

data class PillInfoResponse(
    val header: PillInfoHeader,
    val body: PillInfoBody
)

data class PillInfoHeader(
    val resultCode: String,
    val resultMsg: String
)

data class PillInfoBody(
    val pageNo: Int,
    val totalCount: Int,
    val numOfRows: Int,
    val items: List<PillInfoItem>
)