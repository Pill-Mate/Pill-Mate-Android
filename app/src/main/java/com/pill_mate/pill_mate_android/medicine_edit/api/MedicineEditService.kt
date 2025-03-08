package com.pill_mate.pill_mate_android.medicine_edit.api

import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditInfo
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface MedicineEditService {
    @PUT("/api/v1/management/detail") // 약물 수정
    fun editMedicineInfo(@Body request: MedicineEditInfo): Call<Void>

    @GET("/api/v1/management/detail") // 약물 정보 가져오기
    fun getMedicineInfo(@Query("itemSeq") itemSeq: String): Call<MedicineEditResponse>
}