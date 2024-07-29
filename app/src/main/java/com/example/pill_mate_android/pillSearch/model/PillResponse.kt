package com.example.pill_mate_android.pillSearch.model

data class PillResponse(
    val response: ResponseBody
) {
    data class ResponseBody(
        val header: Header,
        val body: Body
    ) {
        data class Header(
            val resultCode: String,
            val resultMsg: String
        )

        data class Body(
            val numOfRows: Int,
            val pageNo: Int,
            val totalCount: Int,
            val items: List<Pill>
        )
    }
}