package com.pill_mate.pill_mate_android.search.model

interface PillDataSource {
    suspend fun getSearchResults(
        name: String,
        type: SearchType
    ): List<Searchable>

    suspend fun getSearchMedicineResults(itemName: String): List<SearchMedicineItem>
}