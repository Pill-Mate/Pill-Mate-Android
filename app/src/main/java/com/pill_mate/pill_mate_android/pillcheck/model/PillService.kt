package com.pill_mate.pill_mate_android.pillcheck.model

import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.pillsearch.ResponseConflictMedicineDetail
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

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

interface MedicineDetailService {
    @POST("/api/v1/home/medicinedetail")
    fun postMedicineDetailData(@Body medicineId: MedicineIdData): Call<BaseResponse<ResponseMedicineDetail>>
}

interface ConflictMedicineDetailService {
    @GET("/api/v2/search/detail")
    fun getConflictMedicineDetailData(@Query("itemSeq") medicineId: Long): Call<BaseResponse<ResponseConflictMedicineDetail>>
}