package com.example.pill_mate_android.pillSearch.model

import com.example.pill_mate_android.pillSearch.api.PillApiClient

class PillRepository : PillDataSource {
    override suspend fun getPills(serviceKey: String, pageNo: Int, numOfRows: Int, itemName: String): List<Pill>? {
        val response = PillApiClient.pillApiService.getPills(serviceKey, pageNo, numOfRows, itemName)
        return if (response.isSuccessful) {
            response.body()?.response?.body?.items
        } else {
            null
        }
    }
}