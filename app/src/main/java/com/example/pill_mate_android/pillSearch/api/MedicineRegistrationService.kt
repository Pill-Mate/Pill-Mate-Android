package com.example.pill_mate_android.pillSearch.api

import com.example.pill_mate_android.pillSearch.model.EfcyDplctResponse
import com.example.pill_mate_android.pillSearch.model.MedicineRegisterRequest
import com.example.pill_mate_android.pillSearch.model.OnboardingTimeResponse
import com.example.pill_mate_android.pillSearch.model.PharmacyAndHospitalResponse
import com.example.pill_mate_android.pillSearch.model.UsjntTabooResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MedicineRegistrationService {
    @POST("/api/v1/medicine/register") // 약물 등록
    fun registerMedicine(@Body request: MedicineRegisterRequest): Call<Void>

    @GET("/api/v1/medicine/onboarding") // 시간 데이터 가져오기
    fun getMedicineOnboardingTimes(): Call<OnboardingTimeResponse>

    @GET("/api/v1/dur/usjnt-taboo") // 병용금기 데이터 가져오기
    fun getUsjntTaboo(@Query("itemSeq") itemSeq: String): Call<List<UsjntTabooResponse>>

    @GET("/api/v1/dur/efcy-dplct") // 효능군 중복 데이터 가져오기
    fun getEfcyDplct(@Query("itemSeq") itemSeq: String): Call<List<EfcyDplctResponse>>

    @GET("/api/v1/medicine/pharmacy-hospital") // 약국 병원 정보 가져오기
    fun getPharmacyAndHospital(@Query("medicineName") medicineName: String): Call<PharmacyAndHospitalResponse>

    @DELETE("medicines/{name}")
    fun deleteMedicine(@Path("name") medicineName: String): Call<Void>
}