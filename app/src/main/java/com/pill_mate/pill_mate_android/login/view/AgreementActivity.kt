package com.pill_mate.pill_mate_android.login.view

import android.Manifest
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ActivityAgreementBinding
import com.pill_mate.pill_mate_android.login.dialog.AlarmPermissionDialog
import com.pill_mate.pill_mate_android.onboarding.view.TimePicker1Activity
import com.pill_mate.pill_mate_android.util.PermissionUtil.isSystemNotificationPermissionGranted

class AgreementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgreementBinding
    private lateinit var individualCheckBoxes: List<CheckBox>
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            goToTimePicker() // 요청 결과와 상관없이 다음 화면으로 이동
        }

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
        onDetailButtonClick()

    }

    private fun setUpCheckBoxes() {
        individualCheckBoxes = listOf(binding.cb1, binding.cb2, binding.cb3, binding.cb4, binding.cb5, binding.cb6)

        // 개별 체크박스
        individualCheckBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateDoneButtonState()
                updateAllAgreementCheckBox()
                updateAllAgreementBackground()
            }
        } // 모두 동의 체크박스
        binding.cb7.setOnCheckedChangeListener { _, isChecked ->
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
        binding.cb7.setOnCheckedChangeListener(null)
        binding.cb7.isChecked = allChecked
        binding.cb7.setOnCheckedChangeListener { _, isChecked ->
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
        binding.btnDone.isEnabled = allRequiredChecked || binding.cb7.isChecked
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
        binding.btnDone.setOnClickListener { // 시스템 알림 권한이 꺼져 있을 때만 다이얼로그 노출
            if ((binding.cb5.isChecked || binding.cb6.isChecked) && !isSystemNotificationPermissionGranted(this)) {
                val dialog = AlarmPermissionDialog { // 다이얼로그 닫힌 뒤 → 알림 권한 요청
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        goToTimePicker() // 안드로이드 13 미만은 그냥 넘어감
                    }
                }
                dialog.show(supportFragmentManager, "AlarmPermissionDialog")
            } else {
                goToTimePicker()
            }
        }
    }

    // 상세보기 클릭 시
    private fun onDetailButtonClick() {
        binding.tvDetail.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvDetail.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://slashpage.com/pillmate/4z7pvx2kzgqe7mek8653"))
            startActivity(intent)
        }
    }

    private fun goToTimePicker() {
        val intent = Intent(this, TimePicker1Activity::class.java).apply {
            putExtra("ALARM_MARKETING", binding.cb6.isChecked)
            putExtra("ALARM_INFO", binding.cb5.isChecked)
        }
        startActivity(intent)
    }

}