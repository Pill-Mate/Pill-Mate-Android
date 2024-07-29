package com.example.pill_mate_android.pillSearch.model

interface PillDataSource {
    suspend fun getPills(serviceKey: String, pageNo: Int, numOfRows: Int, itemName: String): List<Pill>?
}