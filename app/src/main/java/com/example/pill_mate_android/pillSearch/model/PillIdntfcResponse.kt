package com.example.pill_mate_android.pillSearch.model

data class PillIdntfcResponse(
    val header: PillIdntfcHeader,
    val body: PillIdntfcBody
)

data class PillIdntfcHeader(
    val resultCode: String,
    val resultMsg: String
)

data class PillIdntfcBody(
    val pageNo: Int,
    val totalCount: Int,
    val numOfRows: Int,
    val items: List<PillIdntfcItem>
)