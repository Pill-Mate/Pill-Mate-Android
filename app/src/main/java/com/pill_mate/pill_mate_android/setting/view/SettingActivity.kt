package com.pill_mate.pill_mate_android.setting.view

import android.Manifest.permission
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.user.UserApiClient
import com.pill_mate.pill_mate_android.BaseResponse
import com.pill_mate.pill_mate_android.GlobalApplication
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.ActivitySettingBinding
import com.pill_mate.pill_mate_android.hideLoading
import com.pill_mate.pill_mate_android.login.dialog.AlarmPermissionDialog
import com.pill_mate.pill_mate_android.login.model.KaKaoTokenData
import com.pill_mate.pill_mate_android.login.view.KakaoLoginActivity
import com.pill_mate.pill_mate_android.setting.model.AlarmInfoData
import com.pill_mate.pill_mate_android.setting.model.AlarmMarketingData
import com.pill_mate.pill_mate_android.setting.model.ResponseRoutine
import com.pill_mate.pill_mate_android.setting.model.ResponseUserInfo
import com.pill_mate.pill_mate_android.setting.view.dialog.ConfirmDialogInterface
import com.pill_mate.pill_mate_android.setting.view.dialog.LogoutDialog
import com.pill_mate.pill_mate_android.setting.view.dialog.SettingRoutineDialog
import com.pill_mate.pill_mate_android.setting.view.dialog.SignoutDialog
import com.pill_mate.pill_mate_android.showLoading
import com.pill_mate.pill_mate_android.util.PermissionUtil.isSystemNotificationPermissionGranted
import com.pill_mate.pill_mate_android.util.expandTouchArea
import com.pill_mate.pill_mate_android.util.loadNativeAd
import com.pill_mate.pill_mate_android.util.onFailure
import com.pill_mate.pill_mate_android.util.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity(), ConfirmDialogInterface {

    private lateinit var binding: ActivitySettingBinding
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        }

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

        initView()
        fetchUserInfoData()
        setButtonClickListener()
        switchAlarmToggle()

        // 광고 요청
        loadNativeAd(this, binding.nativeAdContainer)
    }

    private fun initView() {
        binding.btnPersonalRoutine.post {
            binding.btnPersonalRoutine.expandTouchArea(100) // 100dp 만큼 터치 영역 확장
        }

        binding.btnSendComment.post {
            binding.btnSendComment.expandTouchArea(100)
        }

        binding.btnAboutPillmate.post {
            binding.btnAboutPillmate.expandTouchArea(100)
        }

        binding.btnBack.post {
            binding.btnBack.expandTouchArea(100)
        }
    }

    private fun fetchRoutineData(onSuccess: (ResponseRoutine) -> Unit) {
        binding.showLoading()
        val call = ServiceCreator.getRoutineService.getRoutineData()

        call.enqueue(object : Callback<BaseResponse<ResponseRoutine>> {
            override fun onResponse(
                call: Call<BaseResponse<ResponseRoutine>>, response: Response<BaseResponse<ResponseRoutine>>
            ) {
                binding.hideLoading()
                response.body()?.onSuccess { onSuccess(it) }?.onFailure { code, message ->
                    Log.e("Routine API 실패", "code: $code, message: $message")
                }
            }

            override fun onFailure(call: Call<BaseResponse<ResponseRoutine>>, t: Throwable) {
                binding.hideLoading()
                Log.e("네트워크 오류", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun fetchUserInfoData() {
        binding.showLoading()
        val call = ServiceCreator.settingService.getUserInfoData()

        call.enqueue(object : Callback<BaseResponse<ResponseUserInfo>> {
            override fun onResponse(
                call: Call<BaseResponse<ResponseUserInfo>>, response: Response<BaseResponse<ResponseUserInfo>>
            ) {
                binding.hideLoading()
                response.body()?.onSuccess { userInfo ->
                    with(binding) {
                        tvNickname.text = "${userInfo.userName} 님"
                        tvKakaoEmail.text = userInfo.email
                        tgPillmateAlarm.isChecked = userInfo.alarmInfo
                        tgMarketingAlarm.isChecked = userInfo.alarmMarketing

                        Glide.with(root.context).load(userInfo.profileImage).error(R.drawable.img_profile)
                            .into(ivProfile)
                    }
                }?.onFailure { code, message ->
                    Log.e("UserInfo API 실패", "code: $code, message: $message")
                }
            }

            override fun onFailure(call: Call<BaseResponse<ResponseUserInfo>>, t: Throwable) {
                binding.hideLoading()
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

    private fun showSettingRoutineDialog() {
        val settingRoutineDialog = SettingRoutineDialog(this)
        settingRoutineDialog.show(supportFragmentManager, "SettingRoutineDialog")
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
            showSettingRoutineDialog()
        }

        binding.btnAboutPillmate.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://slashpage.com/pillmate"))
            startActivity(intent)
        }

        binding.btnSendComment.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://slashpage.com/pillmate/3p4kj92y41dpkm57q1x8"))
            startActivity(intent)
        }
    }

    private fun searchKakaoToken() {
        UserApiClient.instance.accessTokenInfo { _, error ->
            if (error != null) {
                Log.w("Kakao", "accessToken 만료 + refreshToken도 유효하지 않음 → 재로그인 유도")

                // 재로그인 유도
                UserApiClient.instance.loginWithKakaoAccount(this) { token, loginError ->
                    if (loginError != null) {
                        Toast.makeText(this, "카카오 재로그인에 실패했습니다", Toast.LENGTH_SHORT).show()
                        Log.e("Kakao", "재로그인 실패: $loginError")
                        val intent = Intent(this, KakaoLoginActivity::class.java)
                        startActivity(intent)
                    } else if (token != null) {
                        Log.i("Kakao", "재로그인 성공! ${token.accessToken}")
                        sendKakaoTokenData(KaKaoTokenData(kakaoAccessToken = token.accessToken))
                    }
                }

            } else { // accessToken 유효 or refresh를 통해 갱신 완료
                val accessToken = TokenManagerProvider.instance.manager.getToken()?.accessToken
                if (accessToken != null) {
                    Log.i("Kakao", "최신 accessToken 사용")
                    sendKakaoTokenData(KaKaoTokenData(kakaoAccessToken = accessToken))
                } else {
                    Log.e("Kakao", "토큰이 null입니다. 재로그인 필요")
                    val intent = Intent(this, KakaoLoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    // 회원 탈퇴 api 호출
    private fun sendKakaoTokenData(token: KaKaoTokenData) {
        binding.showLoading()
        val call: Call<Void> = ServiceCreator.signOutService.sendTokenData(token)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                binding.hideLoading()
                if (response.isSuccessful) {
                    GlobalApplication.logout(this@SettingActivity)
                    Log.i(TAG, "서버에서 카카오 연결 끊기 성공")
                    Toast.makeText(applicationContext, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                } else {
                    Log.e(TAG, "서버에서 연결 끊기 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                binding.hideLoading()
                Log.e(TAG, "서버 연결 실패", t)
            }
        })

    }

    private fun switchAlarmToggle() {
        val marketingToggle = binding.tgMarketingAlarm
        val infoToggle = binding.tgPillmateAlarm

        marketingToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isSystemNotificationPermissionGranted(this)) {
                showAlarmPermissionDialog()
            }
            updateMarketingAlarm(isChecked)
        }

        infoToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isSystemNotificationPermissionGranted(this)) {
                showAlarmPermissionDialog()
            }
            updateInfoAlarm(isChecked)
        }
    }

    private fun updateMarketingAlarm(isEnabled: Boolean) {
        val alarmData = AlarmMarketingData(alarmMarketing = isEnabled)

        val call: Call<Void> = ServiceCreator.patchAlarmMarketingService.patchAlarmMarketingData(alarmData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "마케팅 알람 설정 업데이트 성공")
                } else {
                    Log.e(TAG, "마케팅 알람 설정 업데이트 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun updateInfoAlarm(isEnabled: Boolean) {
        val alarmData = AlarmInfoData(alarmInfo = isEnabled)

        val call: Call<Void> = ServiceCreator.patchAlarmInfoService.patchAlarmInfoData(alarmData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "필메이트 알람 설정 업데이트 성공")
                } else {
                    Log.e(TAG, "필메이트 알람 설정 업데이트 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun showAlarmPermissionDialog() {
        val dialog = AlarmPermissionDialog {
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
            }
        }
        dialog.show(supportFragmentManager, "AlarmPermissionDialog")
    }

    override fun onSignOutButtonClick() {
        searchKakaoToken()
    }

    override fun onLogOutButtonClick() {
        GlobalApplication.logout(this@SettingActivity)
    }

    override fun onSettingRoutineButtonClick() {
        fetchRoutineData { responseRoutine ->
            val settingRoutineBottomDialogFragment = SettingRoutineBottomDialogFragment(responseRoutine)
            settingRoutineBottomDialogFragment.show(supportFragmentManager, settingRoutineBottomDialogFragment.tag)
        }
    }
}