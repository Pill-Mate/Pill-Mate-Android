package com.pill_mate.pill_mate_android.pillstats.api

import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.pillstats.model.ResponseMedicineCountTrend
import com.pill_mate.pill_mate_android.pillstats.model.ResponseStatsCalendar
import com.pill_mate.pill_mate_android.pillstats.model.ResponseStatsMedicine
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StatisticsService {
    @GET("/api/v1/statistics/medicines") // 복용 중인 약물 목록 + 누적 복용일수
    fun getMedicines(): Call<BaseResponse<List<ResponseStatsMedicine>>>

    @GET("/api/v1/statistics/medicines/{medicineId}/calendar") // 약물별 월 복용 달력
    fun getCalendar(
        @Path("medicineId") medicineId: Long, @Query("year") year: Int, @Query("month") month: Int
    ): Call<BaseResponse<ResponseStatsCalendar>>

    @GET("/api/v1/statistics/medicine-count-trend") // 약물 종류 갯수 추이 (기본: 이번 달 기준 7개월)
    fun getMedicineCountTrend(): Call<BaseResponse<ResponseMedicineCountTrend>>
}
