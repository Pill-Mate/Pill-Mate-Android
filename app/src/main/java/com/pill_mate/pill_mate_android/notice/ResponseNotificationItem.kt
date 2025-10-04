package com.pill_mate.pill_mate_android.notice

data class ResponseNotificationItem(
    val notificationId: Long,
    val notifyDate: String,
    val notifyTime: String,
    val title: String,
    val notificationRead: Boolean,
    val isFcm: Boolean,
)
