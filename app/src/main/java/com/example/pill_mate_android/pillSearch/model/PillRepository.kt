package com.example.pill_mate_android.pillSearch.model

import android.util.Log
import com.example.pill_mate_android.pillSearch.api.PillApiClient
import retrofit2.HttpException
import java.io.IOException

class PillRepository : PillDataSource {
    override suspend fun getPillInfo(serviceKey: String, pageNo: Int, numOfRows: Int, itemName: String): List<PillInfoItem>? {
        return try {
            val response = PillApiClient.pillApiService.getPillInfo(serviceKey, pageNo, numOfRows, itemName)
            Log.d("PillRepository", "Response code: ${response.code()}")
            Log.d("PillRepository", "Response raw body: ${response.raw()}")
            Log.d("PillRepository", "Response body: ${response.body()}")
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

    override suspend fun getPillIdntfc(serviceKey: String, pageNo: Int, numOfRows: Int, item_name: String): List<PillIdntfcItem>? {
        return try {
            val response = PillApiClient.pillApiService.getPillIdntfc(serviceKey, pageNo, numOfRows, item_name)
            Log.d("PillRepository", "Response code: ${response.code()}")
            Log.d("PillRepository", "Response raw body: ${response.raw()}")
            Log.d("PillRepository", "Response body: ${response.body()}")
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
}