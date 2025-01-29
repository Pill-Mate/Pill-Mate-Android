package com.pill_mate.pill_mate_android.search.api

import com.pill_mate.pill_mate_android.medicine_registration.model.HospitalResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.PharmacyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface XmlApiService {
    @GET("B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire") // xml
    suspend fun getPharmacyList(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("QN") name: String?, // 기관(약국)명
        @Query("ORD") order: String, // 정렬
    ): Response<PharmacyResponse>

    @GET("B552657/HsptlAsembySearchService/getHsptlMdcncListInfoInqire") // xml
    suspend fun getHospitalList(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("QN") name: String?, // 기관(병원)명
        @Query("ORD") order: String, // 정렬
    ): Response<HospitalResponse>
}