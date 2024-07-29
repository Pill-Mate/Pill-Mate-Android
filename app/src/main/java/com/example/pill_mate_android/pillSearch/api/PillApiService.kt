package com.example.pill_mate_android.pillSearch.api

import com.example.pill_mate_android.pillSearch.model.PillResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PillApiService {

    @GET("getDrbEasyDrugList")
    suspend fun getPills(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("itemName") itemName: String,
        @Query("type") type: String = "json"
    ): Response<PillResponse>
}