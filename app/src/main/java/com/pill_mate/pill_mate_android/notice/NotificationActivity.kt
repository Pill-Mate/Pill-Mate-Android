package com.pill_mate.pill_mate_android.notice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.ActivityNotificationBinding
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
        val call: Call<List<ResponseNotificationItem>> = ServiceCreator.notificationService.getNotificationData()

        call.enqueue(object : Callback<List<ResponseNotificationItem>> {
            override fun onResponse(
                call: Call<List<ResponseNotificationItem>>, response: Response<List<ResponseNotificationItem>>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        notificationAdapter.updateList(it)
                    } ?: Log.e("데이터 전송 실패", "서버 응답은 성공했지만 데이터가 없습니다.")
                }
            }

            override fun onFailure(call: Call<List<ResponseNotificationItem>>, t: Throwable) {
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