package com.pill_mate.pill_mate_android.notice

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.ActivityNotificationDetailBinding
import com.pill_mate.pill_mate_android.util.onFailure
import com.pill_mate.pill_mate_android.util.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notification_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val notificationId = intent.getLongExtra("notificationId", -1L)
        if (notificationId != -1L) {
            fetchNotificationDetailData(notificationId)
        } else {
            Log.e("NotificationDetail", "Invalid notificationId")
        }

        setButtonClickListener()
    }

    private fun fetchNotificationDetailData(notificationId: Long) {
        val requestBody = NotificationData(notificationId)
        val call = ServiceCreator.notificationService.getNotificationDetail(requestBody)

        call.enqueue(object : Callback<BaseResponse<ResponseNotificationDetail>> {
            override fun onResponse(
                call: Call<BaseResponse<ResponseNotificationDetail>>,
                response: Response<BaseResponse<ResponseNotificationDetail>>
            ) {
                response.body()?.onSuccess {
                        binding.tvTitle.text = it.title
                        binding.tvDate.text = it.notifyDate
                        binding.tvTime.text = it.notifyTime
                        binding.tvContent.text = it.content
                    }?.onFailure { code, message ->
                        Log.e("API 실패", "code: $code, message: $message")
                    }
            }

            override fun onFailure(call: Call<BaseResponse<ResponseNotificationDetail>>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun setButtonClickListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}