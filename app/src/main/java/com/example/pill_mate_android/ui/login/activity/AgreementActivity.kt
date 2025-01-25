package com.example.pill_mate_android.ui.login.activity

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.ActivityAgreementBinding
import com.example.pill_mate_android.ui.onboarding.TimePicker1Activity

class AgreementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgreementBinding
    private lateinit var individualCheckBoxes: List<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgreementBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

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

    // 뒤로 가기 버튼 클릭 시 -> 로그인 페이지로 이동
    private fun onBackButtonClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    // 가입완료 버튼 클릭 시
    private fun onDoneButtonClick() {
        binding.btnDone.setOnClickListener {
            val intent = Intent(this, TimePicker1Activity::class.java).apply {
                putExtra("ALARM_MARKETING", binding.cb5.isChecked)
            }
            startActivity(intent)
        }
    }

}