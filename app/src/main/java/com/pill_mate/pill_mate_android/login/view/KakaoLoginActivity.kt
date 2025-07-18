package com.pill_mate.pill_mate_android.login.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.FcmTokenManager
import com.pill_mate.pill_mate_android.GlobalApplication
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.ActivityKakaoLoginBinding
import com.pill_mate.pill_mate_android.login.model.KaKaoTokenData
import com.pill_mate.pill_mate_android.login.model.ResponseToken
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.util.onFailure
import com.pill_mate.pill_mate_android.util.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKakaoLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //로그아웃 상태 초기화
        GlobalApplication.resetLogoutFlag()

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

    private fun loginNetwork(accessToken: String) {

        // FCM 토큰 비동기로 받아오기
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            if (fcmToken.isNullOrEmpty()) {
                Log.e("FCM", "FCM 토큰이 비어있습니다. 로그인 요청 생략")
                return@addOnSuccessListener
            }

            val loginData = KaKaoTokenData(kakaoAccessToken = accessToken)
            val call = ServiceCreator.loginService.login(loginData)

            call.enqueue(object : Callback<BaseResponse<ResponseToken>> {
                override fun onResponse(
                    call: Call<BaseResponse<ResponseToken>>, response: Response<BaseResponse<ResponseToken>>
                ) {
                    response.body()?.onSuccess { responseData ->
                            GlobalApplication.saveToken(responseData.jwtToken ?: "")
                            GlobalApplication.saveRefreshToken(responseData.refreshToken)

                            // FCM 토큰 전송
                            FcmTokenManager.sendFcmTokenToServer(fcmToken)
                            Log.d("FCM_TOKEN", "토큰: $fcmToken")

                            val nextActivity = if (responseData.login == true) {
                                MainActivity::class.java
                            } else {
                                AgreementActivity::class.java
                            }

                            navigateToNext(nextActivity)
                        }?.onFailure { code, message ->
                            Log.e("로그인 실패", "code: $code, message: $message")
                        }
                }

                override fun onFailure(call: Call<BaseResponse<ResponseToken>>, t: Throwable) {
                    Log.e("네트워크 오류", "${t.message}")
                }
            })
        }.addOnFailureListener {
            Log.e("FCM", "FCM 토큰 가져오기 실패: ${it.message}")
        }
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
