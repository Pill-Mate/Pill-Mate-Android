package com.example.pill_mate_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ServiceCreator
import com.example.pill_mate_android.databinding.ActivitySuccessBinding
import com.example.pill_mate_android.ui.main.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.success)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onOkButtonClick()
    }

    private fun onOkButtonClick() {
        binding.btnOk.setOnClickListener {
            val wakeupTime = intent.getStringExtra("WAKEUP_TIME")?.toTime()
            val bedTime = intent.getStringExtra("BED_TIME")?.toTime()
            val breakfastTime = intent.getStringExtra("BREAKFAST_TIME")?.toTime()
            val lunchTime = intent.getStringExtra("LUNCH_TIME")?.toTime()
            val dinnerTime = intent.getStringExtra("DINNER_TIME")?.toTime()

            Log.i("Time", "${wakeupTime},${bedTime},${breakfastTime}")

            if (wakeupTime != null && bedTime != null && breakfastTime != null && lunchTime != null && dinnerTime != null) {
                val onBoardingData = OnBoardingData(
                    wakeupTime = wakeupTime,
                    bedTime = bedTime,
                    morningTime = breakfastTime,
                    lunchTime = lunchTime,
                    dinnerTime = dinnerTime
                )

                onBoardingNetwork(onBoardingData)
            } else {
                Toast.makeText(this, "시간 데이터를 확인할 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun String.toTime(): Time? {
        return try {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(this)
            Time(date.time)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun onBoardingNetwork(onBoardingData: OnBoardingData) {

        val call: Call<Void> = ServiceCreator.onBoardingService.sendOnBoardingData(onBoardingData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@SuccessActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("데이터 전송 실패", "데이터 전송 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }
}