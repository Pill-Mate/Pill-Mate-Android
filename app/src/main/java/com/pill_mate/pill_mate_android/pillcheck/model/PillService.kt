package com.pill_mate.pill_mate_android.pillcheck.model

import com.pill_mate.pill_mate_android.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface HomeService {
    @POST("/api/v1/home/scheduledata")
    fun getHomeData(@Body home: HomeData): Call<BaseResponse<ResponseHome>>
}

interface WeeklyCalendarService {
    @POST("/api/v1/home/weekscroll")
    fun getWeeklyCalendarData(@Body home: HomeData): Call<BaseResponse<ResponseWeeklyCalendar>>
}

interface MedicineCheckService {
    @PATCH("/api/v1/home/medicinecheck")
    fun patchCheckData(@Body checkData: List<MedicineCheckData>): Call<BaseResponse<ResponseHome>>
}