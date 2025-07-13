package com.pill_mate.pill_mate_android.notice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.ActivityNotificationBinding
import com.pill_mate.pill_mate_android.util.onFailure
import com.pill_mate.pill_mate_android.util.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var binding: ActivityNotificationBinding

    private val notificationItemDataList = mutableListOf<ResponseNotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notification)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initAdapter()
        fetchNotificationItemData()
        setButtonClickListener()
    }

    private fun initAdapter() {
        notificationAdapter = NotificationAdapter(notificationItemDataList) { item ->
            val intent = Intent(this, NotificationDetailActivity::class.java)
            intent.putExtra("notificationId", item.notificationId)
            startActivity(intent)
        }
        binding.rvNotification.adapter = notificationAdapter
    }

    private fun fetchNotificationItemData() {
        val call = ServiceCreator.notificationService.getNotificationData()

        call.enqueue(object : Callback<BaseResponse<List<ResponseNotificationItem>>> {
            override fun onResponse(
                call: Call<BaseResponse<List<ResponseNotificationItem>>>,
                response: Response<BaseResponse<List<ResponseNotificationItem>>>
            ) {
                response.body()?.onSuccess {
                        notificationAdapter.updateList(it)
                    }?.onFailure { code, message ->
                        Log.e("API 실패", "code: $code, message: $message")
                    }
            }

            override fun onFailure(call: Call<BaseResponse<List<ResponseNotificationItem>>>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun setButtonClickListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchNotificationItemData()
    }
}