package com.pill_mate.pill_mate_android

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pill_mate.pill_mate_android.main.view.MainActivity

class PillMateFcmService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "PillMateFcmService"
        private const val CHANNEL_ID = "pillmate_channel"
        private const val CHANNEL_NAME = "PillMate 알림"
    }

    // FCM 토큰이 새로 발급될 때 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken(): $token")
        FcmTokenManager.sendFcmTokenToServer(token)
    }

    // 푸시 메시지를 수신할 때 호출
    @RequiresApi(VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "PillMate 알림"

        val message = remoteMessage.notification?.body ?: remoteMessage.data["message"] ?: "복약 알림이 도착했어요!"

        Log.d(TAG, "FCM 메시지 수신 - title: $title, message: $message")

        showNotification(title, message)
    }

    //알림 생성 및 표시
    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Android 8.0 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "PillMate 복약 알림 채널"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.img_fcm_logo).setContentTitle(title)
                .setContentText(message).setContentIntent(pendingIntent).setAutoCancel(true).build()

        if (ActivityCompat.checkSelfPermission(
                this, permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
    }
}