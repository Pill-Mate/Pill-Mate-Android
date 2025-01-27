package com.example.pill_mate_android

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pill_mate_android.databinding.ActivitySettingBinding
import com.example.pill_mate_android.ui.login.KaKaoTokenData
import com.example.pill_mate_android.ui.login.activity.KakaoLoginActivity
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity(), ConfirmDialogInterface {

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

        //fetchUserInfoData()
        setButtonClickListener()
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

    private fun showSignoutDialog() {
        val signOutDialog = SignoutDialog(this)
        signOutDialog.show(supportFragmentManager, "SignoutDialog")
    }

    private fun showLogoutDialog() {
        val logOutDialog = LogoutDialog(this)
        logOutDialog.show(supportFragmentManager, "LogoutDialog")
    }

    private fun setButtonClickListener() { // 뒤로 가기 버튼 클릭 시 -> 복약체크 페이지로 이동
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvSignout.setOnClickListener {
            showSignoutDialog()
        }

        binding.tvLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnPersonalRoutine.setOnClickListener {
            val settingRoutineBottomDialogFragment: SettingRoutineBottomDialogFragment =
                SettingRoutineBottomDialogFragment {}
            settingRoutineBottomDialogFragment.show(supportFragmentManager, settingRoutineBottomDialogFragment.tag)
        }
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("kakao_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("kakao_access_token", null)
    }

    private fun searchKakaoToken() {
        val accessToken = getAccessToken()

        if (accessToken != null) {
            Log.i(TAG, "AccessToken 조회 성공: $accessToken") // 서버로 accessToken 전달
            sendKakaoTokenData(KaKaoTokenData(kakaoAccessToken = accessToken))
        } else {
            Log.e(TAG, "AccessToken 조회 실패")
            Toast.makeText(applicationContext, "AccessToken 조회 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendKakaoTokenData(token: KaKaoTokenData) {
        val call: Call<Void> = ServiceCreator.signOutService.sendTokenData(token)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "서버에서 카카오 연결 끊기 성공")
                    Toast.makeText(applicationContext, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SettingActivity, KakaoLoginActivity::class.java)
                    startActivity(intent)

                } else {
                    Log.e(TAG, "서버에서 연결 끊기 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "서버 연결 실패", t)
            }
        })

    }

    private fun logoutNetwork() {
        val call: Call<Void> = ServiceCreator.logOutService.logout()

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.d("카카오", "카카오 로그아웃 실패")
                        } else {
                            Log.d("카카오", "카카오 로그아웃 성공!")
                            val intent = Intent(this@SettingActivity, KakaoLoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    Log.e(TAG, "데이터 전송 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    override fun onSignOutButtonClick() {
        searchKakaoToken()
    }

    override fun onLogOutButtonClick() {
        logoutNetwork()
    }
}