package com.pill_mate.pill_mate_android.medicine_edit.api

import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditInfo
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MedicineEditService {
    @PUT("/api/v1/management/detail/{scheduleId}") // itemSeq 추가!
    fun editMedicineInfo(@Path("scheduleId") scheduleId: Long, @Body request: MedicineEditInfo): Call<Void>


    @GET("/api/v1/management/detail/{scheduleId}") // 약물 정보
    fun getMedicineInfo(@Path("scheduleId") scheduleId: Long): Call<MedicineEditResponse>
}