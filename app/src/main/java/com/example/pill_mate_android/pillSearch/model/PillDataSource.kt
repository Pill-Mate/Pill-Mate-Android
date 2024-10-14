package com.example.pill_mate_android.pillSearch.model

interface PillDataSource {
    suspend fun getPillInfo(serviceKey: String, pageNo: Int, numOfRows: Int, itemName: String): List<PillInfoItem>?
    suspend fun getPillIdntfc(serviceKey: String, pageNo: Int, numOfRows: Int, item_name: String): List<PillIdntfcItem>?
    suspend fun getPharmacyList(serviceKey: String, pageNo: Int, numOfRows: Int, name: String?, order: String): List<PharmacyItem>?
}