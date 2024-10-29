package com.example.pill_mate_android.ui.login.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.GlobalApplication
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ServiceCreator
import com.example.pill_mate_android.databinding.ActivityAgreementBinding
import com.example.pill_mate_android.ui.login.LoginData
import com.example.pill_mate_android.ui.login.ResponseToken
import com.example.pill_mate_android.ui.onboarding.TimePicker1Activity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgreementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgreementBinding
    private lateinit var individualCheckBoxes: List<CheckBox>
    private var accessToken: String? = null // 전달받은 토큰

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgreementBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 인텐트로부터 accessToken을 가져오기
        accessToken = intent.getStringExtra("ACCESS_TOKEN")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.agreement)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpCheckBoxes()
        onDoneButtonClick()
        onBackButtonClick()

    }

    private fun setUpCheckBoxes() {
        individualCheckBoxes = listOf(binding.cb1, binding.cb2, binding.cb3, binding.cb4, binding.cb5)

        // 개별 체크박스
        individualCheckBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateDoneButtonState()
                updateAllAgreementCheckBox()
                updateAllAgreementBackground()
            }
        } // 모두 동의 체크박스
        binding.cb6.setOnCheckedChangeListener { _, isChecked ->
            individualCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = isChecked
            }
            updateDoneButtonState()
            updateAllAgreementBackground()
        }
    }

    // 모두 동의 체크박스 상태 변경
    private fun updateAllAgreementCheckBox() {
        val allChecked = individualCheckBoxes.all { it.isChecked }
        binding.cb6.setOnCheckedChangeListener(null)
        binding.cb6.isChecked = allChecked
        binding.cb6.setOnCheckedChangeListener { _, isChecked ->
            individualCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = isChecked
            }
            updateAllAgreementBackground()
        }
        updateDoneButtonState()
    }

    //모든 필수 체크박스가 체크된 경우 완료 버튼 활성화
    private fun updateDoneButtonState() {
        val allRequiredChecked = individualCheckBoxes.take(4).all { it.isChecked }
        binding.btnDone.isEnabled = allRequiredChecked || binding.cb6.isChecked
        updateDoneButtonBackground(binding.btnDone.isEnabled)

    }

    // 가입 완료 버튼 색상 변경
    private fun updateDoneButtonBackground(isEnabled: Boolean) {
        binding.btnDone.backgroundTintList = getColorStateList(
            if (isEnabled) R.color.main_blue_1 else R.color.gray_3
        )
        binding.btnDone.setTextColor(getColor(if (isEnabled) R.color.white else R.color.black))
    }

    // 모두 동의 체크박스 배경 색상 변경
    private fun updateAllAgreementBackground() {
        val allChecked = individualCheckBoxes.all { it.isChecked }
        binding.layoutCbAll.setBackgroundResource(
            if (allChecked) R.drawable.shape_round_box_on else R.drawable.shape_round_box_off
        )
    }

    private fun loginNetwork() {

        val loginData = LoginData(
            kakaoAccessToken = accessToken, marketingAlarm = binding.cb5.isChecked
        )
        val call: Call<ResponseToken> = ServiceCreator.loginService.login(loginData)

        call.enqueue(object : Callback<ResponseToken> {
            override fun onResponse(
                call: Call<ResponseToken>, response: Response<ResponseToken>
            ) {

                if (response.isSuccessful) {
                    response.body()?.jwtToken?.let { jwtToken ->
                        saveJwtToken(jwtToken)

                        Toast.makeText(this@AgreementActivity, "가입 완료", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@AgreementActivity, TimePicker1Activity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@AgreementActivity, "가입 실패 : ${response.message()}", Toast.LENGTH_SHORT).show()
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

    // 뒤로 가기 버튼 클릭 시 -> 로그인 페이지로 이동
    private fun onBackButtonClick() {
        binding.btnBack.setOnClickListener {
            val intent = Intent(this@AgreementActivity, KakaoLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 가입완료 버튼 클릭 시
    private fun onDoneButtonClick() {
        binding.btnDone.setOnClickListener {
            loginNetwork()
        }
    }

}