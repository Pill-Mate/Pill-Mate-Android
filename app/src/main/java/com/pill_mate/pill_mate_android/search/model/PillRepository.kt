package com.pill_mate.pill_mate_android.search.model

import android.util.Log
import com.pill_mate.pill_mate_android.ServiceCreator

class PillRepository : PillDataSource {

    override suspend fun getSearchResults(
        name: String,
        type: SearchType
    ): List<Searchable> {
        return try {
            when (type) {
                SearchType.PHARMACY -> {
                    val response = ServiceCreator.searchService.searchPharmacy(name)
                    Log.d("PillRepository", "요청 보냄: /api/v1/medicine/pharmacy?name=$name")
                    Log.d("PillRepository", "SearchPharmacy response: $response")

                    if (response.isSuccess) {
                        response.result.map { item ->
                            object : Searchable {
                                override fun getName(): String = item.name
                                override fun getAddress(): String = item.address
                                override fun getNumber(): String = item.phone
                            }
                        }
                    } else {
                        emptyList()
                    }
                }

                SearchType.HOSPITAL -> {
                    val response = ServiceCreator.searchService.searchHospital(name)
                    Log.d("PillRepository", "HOSPITAL 요청: /api/v1/medicine/hospital?name=$name")
                    Log.d("PillRepository", "HOSPITAL 응답 isSuccess: ${response.isSuccess}, result size: ${response.result?.size}")
                    Log.d("PillRepository", "HOSPITAL 응답 전체: $response")
                    if (response.isSuccess) {
                        response.result.map { item ->
                            object : Searchable {
                                override fun getName(): String = item.name
                                override fun getAddress(): String = item.address
                                override fun getNumber(): String = item.phone
                            }
                        }
                    } else {
                        emptyList()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PillRepository", "Error fetching results for $type", e)
            emptyList()
        }
    }

    override suspend fun getSearchMedicineResults(itemName: String): List<SearchMedicineItem> {
        return try {
            val response = ServiceCreator.searchService.searchPill(itemName)
            Log.d("PillRepository", "SearchPill response: $response")

            if (response.isSuccess) {
                response.result
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PillRepository", "Error fetching pill search results", e)
            emptyList()
        }
    }
}