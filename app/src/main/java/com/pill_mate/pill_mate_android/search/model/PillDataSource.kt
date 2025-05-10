package com.pill_mate.pill_mate_android.search.model

interface PillDataSource {
    suspend fun getPillIdntfc(
        serviceKey: String,
        pageNo: Int,
        numOfRows: Int,
        item_name: String
    ): List<PillIdntfcItem>?

    suspend fun getSearchResults(
        serviceKey: String,
        pageNo: Int,
        numOfRows: Int,
        name: String?,
        order: String,
        type: SearchType
    ): List<Searchable>?
}