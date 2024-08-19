package com.example.pill_mate_android.pillSearch.api

import com.example.pill_mate_android.pillSearch.model.PillIdntfcResponse
import com.example.pill_mate_android.pillSearch.model.PillInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PillApiService {

    @GET("1471000/DrbEasyDrugInfoService/getDrbEasyDrugList")
    suspend fun getPillInfo(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("itemName") itemName: String,
        @Query("type") type: String = "json"
    ): Response<PillInfoResponse>

    @GET("1471000/MdcinGrnIdntfcInfoService01/getMdcinGrnIdntfcInfoList01")
    suspend fun getPillIdntfc(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("item_name") itemName: String,
        @Query("type") type: String = "json"
    ): Response<PillIdntfcResponse>
}