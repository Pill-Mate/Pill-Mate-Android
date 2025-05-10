package com.pill_mate.pill_mate_android.search.model

import android.util.Log
import com.pill_mate.pill_mate_android.medicine_registration.model.HospitalResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.PharmacyResponse
import com.pill_mate.pill_mate_android.search.api.JsonApiClient
import com.pill_mate.pill_mate_android.search.api.XmlApiClient
import retrofit2.HttpException
import retrofit2.Response
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
        serviceKey: String,
        pageNo: Int,
        numOfRows: Int,
        name: String?,
        order: String,
        type: SearchType
    ): List<Searchable>? {
        return try {
            when (type) {
                SearchType.PHARMACY -> {
                    val response: Response<PharmacyResponse> = XmlApiClient.xmlApiService.getPharmacyList(
                        serviceKey, pageNo, numOfRows, name, order
                    )
                    response.body()?.body?.items?.itemList
                }
                SearchType.HOSPITAL -> {
                    val response: Response<HospitalResponse> = XmlApiClient.xmlApiService.getHospitalList(
                        serviceKey, pageNo, numOfRows, name, order
                    )
                    response.body()?.body?.items?.itemList
                }
            }
        } catch (e: Exception) {
            Log.e("PillRepository", "Error fetching results for $type", e)
            null
        }
    }
}