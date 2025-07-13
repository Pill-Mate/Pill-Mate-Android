package com.pill_mate.pill_mate_android.notice

import com.pill_mate.pill_mate_android.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationService {
    @GET("/api/v1/alarm/notification")
    fun getNotificationData(): Call<BaseResponse<List<ResponseNotificationItem>>>

    @POST("/api/v1/alarm/notificationDetail")
    fun getNotificationDetail(@Body request: NotificationData): Call<BaseResponse<ResponseNotificationDetail>>
}