package com.example.pill_mate_android.ui.pillcheck

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HomeService {
    @POST("/api/v1/home/scheduledata")
    fun getHomeData(@Body home: HomeData): Call<ResponseHome>
}

interface WeeklyCalendarService {
    @POST("/api/v1/home/weekscroll")
    fun getWeeklyCalendarData(@Body home: HomeData): Call<ResponseWeeklyCalendar>
}