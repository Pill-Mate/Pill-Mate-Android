package com.pill_mate.pill_mate_android.ui.pilledit

import retrofit2.Call
import retrofit2.http.GET

interface ActiveMedicineService {
    @GET("/api/v1/management/home/current")
    fun getActiveMedicineList(): Call<ResponseActiveMedicine>
}

interface InActiveMedicineService {
    @GET("/api/v1/management/home/stop")
    fun getInActiveMedicineList(): Call<ResponseInActiveMedicine>
}