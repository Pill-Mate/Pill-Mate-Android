package com.pill_mate.pill_mate_android.login.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.GlobalApplication
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.login.model.ResponseAuthStatus
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.util.onFailure
import com.pill_mate.pill_mate_android.util.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jwtToken = GlobalApplication.getToken()

        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({ // jwtToken 보유 여부에 따라 다음 페이지로 이동
            if (jwtToken.isEmpty()) {
                navigateTo(KakaoLoginActivity::class.java)
            } else {
                checkAuthStatus() // 로그인/온보딩 완료 여부를 서버에 확인 후 분기
            }
        }, 3000)
    }

    private fun checkAuthStatus() {
        ServiceCreator.authStatusService.checkStatus()
            .enqueue(object : Callback<BaseResponse<ResponseAuthStatus>> {
                override fun onResponse(
                    call: Call<BaseResponse<ResponseAuthStatus>>,
                    response: Response<BaseResponse<ResponseAuthStatus>>
                ) {
                    response.body()?.onSuccess { status ->
                        val nextActivity = if (status.onboarded) {
                            MainActivity::class.java
                        } else {
                            AgreementActivity::class.java // 온보딩 미완료 시 온보딩 첫 화면으로 이동
                        }
                        navigateTo(nextActivity)
                    }?.onFailure { code, message ->
                        Log.e("인증 상태 확인 실패", "code: $code, message: $message")
                        navigateTo(KakaoLoginActivity::class.java)
                    }
                }

                override fun onFailure(call: Call<BaseResponse<ResponseAuthStatus>>, t: Throwable) {
                    Log.e("네트워크 오류", "${t.message}")
                    navigateTo(KakaoLoginActivity::class.java)
                }
            })
    }

    private fun navigateTo(activity: Class<*>) {
        startActivity(Intent(this, activity))
        finish()
    }
}