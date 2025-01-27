package com.example.pill_mate_android.ui.login.activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.GlobalApplication
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ServiceCreator
import com.example.pill_mate_android.databinding.ActivityKakaoLoginBinding
import com.example.pill_mate_android.ui.login.KaKaoTokenData
import com.example.pill_mate_android.ui.login.ResponseToken
import com.example.pill_mate_android.ui.main.activity.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKakaoLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kakao_login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onKakaoLoginButtonClick()

    }

    private fun kakaoLogin() { // 로그인 정보 확인
        // 카카오 로그인
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                saveAccessToken(token.accessToken) // 토큰 저장
                loginNetwork(token.accessToken)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")

                    loginNetwork(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun saveAccessToken(accessToken: String) {
        val sharedPreferences = getSharedPreferences("kakao_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("kakao_access_token", accessToken).apply()
    }

    private fun loginNetwork(accessToken: String) {

        val loginData = KaKaoTokenData(kakaoAccessToken = accessToken)
        val call: Call<ResponseToken> = ServiceCreator.loginService.login(loginData)

        call.enqueue(object : Callback<ResponseToken> {
            override fun onResponse(
                call: Call<ResponseToken>, response: Response<ResponseToken>
            ) {

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData?.jwtToken != null) {
                        saveJwtToken(responseData.jwtToken)

                        Log.i("가입 성공", "가입 성공 ${responseData.jwtToken}")

                        val nextActivity = if (responseData.login == true) {
                            MainActivity::class.java
                        } else {
                            AgreementActivity::class.java
                        }

                        // 기존 사용자, 신규 사용자 여부에 따라 다음 페이지로 이동
                        navigateToNext(nextActivity)
                    }
                } else {
                    Log.e("가입 실패", "가입 실패 : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseToken>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    // jwtToken 저장
    private fun saveJwtToken(token: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("JWT_TOKEN", token)
        editor.apply()

        // GlobalApplication에 토큰 저장
        GlobalApplication.getInstance()?.userToken = token
    }

    // 카카오 로그인 버튼 클릭 시
    private fun onKakaoLoginButtonClick() {
        binding.btnKakaoLogin.setOnClickListener {
            kakaoLogin()
        }
    }

    // 로그인 -> 다음 페이지로 이동
    private fun navigateToNext(nextActivity: Class<*>) {
        val intent = Intent(this, nextActivity)
        startActivity(intent)
    }

}