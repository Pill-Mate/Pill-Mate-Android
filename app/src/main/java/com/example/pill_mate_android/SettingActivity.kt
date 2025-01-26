package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pill_mate_android.databinding.ActivitySettingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchUserInfoData()
        onBackButtonClick()
    }

    private fun fetchUserInfoData() {
        val call: Call<ResponseUserInfo> = ServiceCreator.settingService.getUserInfoData()

        call.enqueue(object : Callback<ResponseUserInfo> {
            override fun onResponse(
                call: Call<ResponseUserInfo>, response: Response<ResponseUserInfo>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let { userInfo ->
                        with(binding) {
                            tvNickname.text = userInfo.userName + " 님"
                            tvKakaoEmail.text = userInfo.email

                            Glide.with(root.context).load(userInfo.profileImage).error(R.drawable.img_profile)
                                .into(ivProfile)
                        }
                    } ?: Log.e("데이터 받아오기 실패", "데이터 받아오기 실패")
                }
            }

            override fun onFailure(call: Call<ResponseUserInfo>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 뒤로 가기 버튼 클릭 시 -> 복약체크 페이지로 이동
    private fun onBackButtonClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}