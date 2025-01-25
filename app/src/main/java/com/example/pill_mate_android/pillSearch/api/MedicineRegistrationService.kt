package com.example.pill_mate_android.pillSearch.api

import com.example.pill_mate_android.pillSearch.model.ConflictResponse
import com.example.pill_mate_android.pillSearch.model.MedicineRegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MedicineRegistrationService {
    @Headers("Content-Type: application/json")
    @POST("/api/v1/medicine-register/regular") // 약물 등록
    fun registerMedicine(@Body request: MedicineRegisterRequest): Call<Void>

    @GET("medicine/conflicts")
    fun getMedicineConflicts(@Query("identifyNumber") identifyNumber: String): Call<ConflictResponse>

    @DELETE("medicines/{name}")
    fun deleteMedicine(@Path("name") medicineName: String): Call<Void>
}