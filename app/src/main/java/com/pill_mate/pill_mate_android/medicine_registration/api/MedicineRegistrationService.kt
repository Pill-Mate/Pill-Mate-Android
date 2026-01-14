package com.pill_mate.pill_mate_android.medicine_registration.api

import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictCheckResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictRemoveResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.MedicineRegisterRequest
import com.pill_mate.pill_mate_android.medicine_registration.model.OnboardingTimeResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.PhoneAndAddressResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.PillCountCheckResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MedicineRegistrationService {
    @GET("/api/v1/medicine/pill-count-check") // 등록한 약물 개수 4 초과여부
    fun checkPillCount(): Call<PillCountCheckResponse>

    @POST("/api/v1/medicine/register") // 약물 등록
    fun registerMedicine(@Body request: MedicineRegisterRequest): Call<Void>

    @GET("/api/v1/medicine/onboarding") // 시간 데이터 가져오기
    fun getMedicineOnboardingTimes(): Call<OnboardingTimeResponse>

    @GET("/api/v1/dur/check-conflict") // 약물 충돌 총합
    fun checkConflict(@Query("itemSeq") itemSeq: Long): Call<ConflictCheckResponse>

    @GET("/api/v1/dur/get-phone-address") // 약국 병원 전화번호 및 주소 정보 가져오기
    fun getPhoneAndAddress(@Query("itemSeq") itemSeq: String): Call<PhoneAndAddressResponse>

    @DELETE("/api/v1/dur/conflict-remove") // 충돌 약국 제거하기
    fun removeConflict(@Query("itemSeq") itemSeq: String): Call<ConflictRemoveResponse>
}