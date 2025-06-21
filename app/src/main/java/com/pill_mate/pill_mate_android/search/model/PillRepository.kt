package com.pill_mate.pill_mate_android.search.model

import android.util.Log
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.search.api.JsonApiClient
import retrofit2.HttpException
import java.io.IOException

class PillRepository : PillDataSource {
    override suspend fun getPillIdntfc(
        serviceKey: String,
        pageNo: Int,
        numOfRows: Int,
        item_name: String
    ): List<PillIdntfcItem>? {
        return try {
            val response = JsonApiClient.jsonApiService.getPillIdntfc(serviceKey, pageNo, numOfRows, item_name)

            if (response.isSuccessful) {
                val pills = response.body()?.body?.items
                Log.d("PillRepository", "Fetched pills: $pills")
                pills
            } else {
                Log.e("PillRepository", "Error fetching pills: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: HttpException) {
            Log.e("PillRepository", "HTTP exception", e)
            null
        } catch (e: IOException) {
            Log.e("PillRepository", "Network exception", e)
            null
        } catch (e: Exception) {
            Log.e("PillRepository", "Unexpected exception", e)
            null
        }
    }

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
}