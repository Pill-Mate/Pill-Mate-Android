package com.pill_mate.pill_mate_android.search.api

import com.pill_mate.pill_mate_android.search.model.SearchHospitalResponse
import com.pill_mate.pill_mate_android.search.model.SearchPharmacyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/api/v1/medicine/search/pharmacy")
    suspend fun searchPharmacy(@Query("name") name: String): SearchPharmacyResponse

    @GET("/api/v1/medicine/search/hospital")
    suspend fun searchHospital(@Query("name") name: String): SearchHospitalResponse
}
