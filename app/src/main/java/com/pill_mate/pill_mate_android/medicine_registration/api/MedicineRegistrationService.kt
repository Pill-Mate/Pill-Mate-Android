package com.pill_mate.pill_mate_android.medicine_registration.api

import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictRemoveResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.MedicineRegisterRequest
import com.pill_mate.pill_mate_android.medicine_registration.model.OnboardingTimeResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.PhoneAndAddressResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("/api/v1/dur/get-phone-address") // 약국 병원 전화번호 및 주소 정보 가져오기
    fun getPhoneAndAddress(@Query("itemSeq") itemSeq: String): Call<PhoneAndAddressResponse>

    @DELETE("/api/v1/dur/conflict-remove") // 충돌 약국 제거하기
    fun removeConflict(@Query("itemSeq") itemSeq: String): Call<ConflictRemoveResponse>
}