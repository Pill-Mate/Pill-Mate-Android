package com.pill_mate.pill_mate_android.pilledit.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ActiveMedicineService {
    @GET("/api/v1/management/home/current")
    fun getActiveMedicineList(): Call<ResponseActiveMedicine>
}

interface InActiveMedicineService {
    @GET("/api/v1/management/home/stop")
    fun getInActiveMedicineList(): Call<ResponseInActiveMedicine>
}

interface StopMedicineService {
    @PATCH("/api/v1/management/home/current/{scheduleId}")
    fun patchStopMedicineData(@Path("scheduleId") scheduleId: Long): Call<Void>
}