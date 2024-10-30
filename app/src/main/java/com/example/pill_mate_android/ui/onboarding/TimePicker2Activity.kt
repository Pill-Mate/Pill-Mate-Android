package com.example.pill_mate_android.ui.onboarding

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ServiceCreator
import com.example.pill_mate_android.databinding.ActivityTimePicker2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimePicker2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTimePicker2Binding
    private var isBreakfastTimePickerVisible = true
    private var isLunchTimePickerVisible = false
    private var isDinnerTimePickerVisible = false
    private val minValues = arrayOf("00", "10", "20", "30", "40", "50")
    private val amPmValues = arrayOf("오전", "오후")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimePicker2Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.timepicker2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        onInitButtonClick()
        onBackButtonClick()
        onNextButtonClick()

    }

    private fun initView() { // NumberPicker 초기화
        with(binding) {
            initNumberPicker(hrsPicker1, 1, 12, 9) { updateBreakfastTime() }
            initNumberPicker(minPicker1, 0, minValues.size - 1, 0, minValues) { updateBreakfastTime() }
            initNumberPicker(amPmPicker1, 0, amPmValues.size - 1, 0, amPmValues) { updateBreakfastTime() }

            initNumberPicker(hrsPicker2, 1, 12, 12) { updateLunchTime() }
            initNumberPicker(minPicker2, 0, minValues.size - 1, 0, minValues) { updateLunchTime() }
            initNumberPicker(amPmPicker2, 0, amPmValues.size - 1, 1, amPmValues) { updateLunchTime() }

            initNumberPicker(hrsPicker3, 1, 12, 8) { updateDinnerTime() }
            initNumberPicker(minPicker3, 0, minValues.size - 1, 0, minValues) { updateDinnerTime() }
            initNumberPicker(amPmPicker3, 0, amPmValues.size - 1, 1, amPmValues) { updateDinnerTime() }

            val fontResId = R.font.pretendard_bold // NumberPicker 커스터마이징
            customizeNumberPicker(hrsPicker1, fontResId)
            customizeNumberPicker(minPicker1, fontResId)
            customizeNumberPicker(amPmPicker1, fontResId)
            customizeNumberPicker(hrsPicker2, fontResId)
            customizeNumberPicker(minPicker2, fontResId)
            customizeNumberPicker(amPmPicker2, fontResId)
            customizeNumberPicker(hrsPicker3, fontResId)
            customizeNumberPicker(minPicker3, fontResId)
            customizeNumberPicker(amPmPicker3, fontResId)
        }

        // 드롭다운 초기 상태 설정
        setTimePickerVisibility(isBreakfastTimePickerVisible, isLunchTimePickerVisible, isDinnerTimePickerVisible)
    }

    private fun onInitButtonClick() { // 아침 시간 드롭다운 버튼 클릭 시
        binding.btnDropdown1.setOnClickListener {
            isBreakfastTimePickerVisible = !isBreakfastTimePickerVisible
            isLunchTimePickerVisible = // 아침 드롭다운이 닫히면 점심 드롭다운 열기
                !isBreakfastTimePickerVisible
            setTimePickerVisibility(
                isBreakfastTimePickerVisible, isLunchTimePickerVisible, false
            )
        }

        // 점심 시간 드롭다운 버튼 클릭 시
        binding.btnDropdown2.setOnClickListener {
            isLunchTimePickerVisible = !isLunchTimePickerVisible
            isDinnerTimePickerVisible = // 점심 드롭다운이 닫히면 저녁 드롭다운 열기
                !isLunchTimePickerVisible
            setTimePickerVisibility(
                false, isLunchTimePickerVisible, isDinnerTimePickerVisible
            )
        }

        // 저녁 시간 드롭다운 버튼 클릭 시
        binding.btnDropdown3.setOnClickListener {
            isDinnerTimePickerVisible = !isDinnerTimePickerVisible
            if (isDinnerTimePickerVisible) {
                isBreakfastTimePickerVisible = false
                isLunchTimePickerVisible = false
            } else { // 저녁 드롭다운이 닫히면 아침 드롭다운 열기
                isBreakfastTimePickerVisible = true
                isLunchTimePickerVisible = false
            }
            setTimePickerVisibility(
                isBreakfastTimePickerVisible, false, isDinnerTimePickerVisible
            )
        }
    }

    private fun initNumberPicker(
        picker: NumberPicker,
        minValue: Int,
        maxValue: Int,
        defaultValue: Int,
        displayedValues: Array<String>? = null,
        updateTime: () -> Unit
    ) {
        picker.apply {
            this.minValue = minValue
            this.maxValue = maxValue
            wrapSelectorWheel = true
            value = defaultValue
            if (displayedValues != null) {
                this.displayedValues = displayedValues
            }
            setOnValueChangedListener { _, _, _ -> updateTime() }
        }
    }

    private fun setTimePickerVisibility(showBreakfast: Boolean, showLunch: Boolean, showDinner: Boolean) {
        showBreakfastTimePicker(showBreakfast)
        showLunchTimePicker(showLunch)
        showDinnerTimePicker(showDinner)
    }

    private fun showBreakfastTimePicker(show: Boolean) {
        with(binding) {
            layoutTpBreakfast.visibility = if (show) View.VISIBLE else View.GONE
            line1.visibility = if (show) View.GONE else View.VISIBLE
            btnDropdown1.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showLunchTimePicker(show: Boolean) {
        with(binding) {
            layoutTpLunch.visibility = if (show) View.VISIBLE else View.GONE
            line2.visibility = if (show) View.GONE else View.VISIBLE
            btnDropdown2.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showDinnerTimePicker(show: Boolean) {
        with(binding) {
            layoutTpDinner.visibility = if (show) View.VISIBLE else View.GONE
            btnDropdown3.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun updateBreakfastTime() {
        val amPm = amPmValues[binding.amPmPicker1.value]
        val hour = binding.hrsPicker1.value
        val min = minValues[binding.minPicker1.value]
        binding.tvBreakfastTime.text = "$amPm $hour:$min"
    }

    private fun updateLunchTime() {
        val amPm = amPmValues[binding.amPmPicker2.value]
        val hour = binding.hrsPicker2.value
        val min = minValues[binding.minPicker2.value]
        binding.tvLunchTime.text = "$amPm $hour:$min"
    }

    private fun updateDinnerTime() {
        val amPm = amPmValues[binding.amPmPicker3.value]
        val hour = binding.hrsPicker3.value
        val min = minValues[binding.minPicker3.value]
        binding.tvDinnerTime.text = "$amPm $hour:$min"
    }

    private fun onNextButtonClick() {
        binding.btnNext.setOnClickListener {
            val breakfastTime = getFormattedTime(binding.hrsPicker1, binding.minPicker1, binding.amPmPicker1)
            val lunchTime = getFormattedTime(binding.hrsPicker2, binding.minPicker2, binding.amPmPicker2)
            val dinnerTime = getFormattedTime(binding.hrsPicker3, binding.minPicker3, binding.amPmPicker3)

            val wakeupTime = intent.getStringExtra("WAKEUP_TIME")
            val bedTime = intent.getStringExtra("BED_TIME")
            val alarmMarketing = intent.getBooleanExtra("ALARM_MARKETING", false)

            val onBoardingData = OnBoardingData(
                alarmMarketing = alarmMarketing,
                wakeupTime = wakeupTime,
                bedTime = bedTime,
                morningTime = breakfastTime,
                lunchTime = lunchTime,
                dinnerTime = dinnerTime

            )

            onBoardingNetwork(onBoardingData)
        }
    }

    private fun onBackButtonClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun customizeNumberPicker(numberPicker: NumberPicker, fontResId: Int) {
        try {
            val typeface = ResourcesCompat.getFont(numberPicker.context, fontResId)

            val fields = NumberPicker::class.java.declaredFields
            for (field in fields) {
                if (field.name == "mSelectionDivider") {
                    field.isAccessible = true
                    field.set(numberPicker, null)
                }
                if (field.name == "mSelectorWheelPaint") {
                    field.isAccessible = true
                    val paint = field.get(numberPicker) as? Paint
                    paint?.typeface = typeface
                }
                if (field.name == "mInputText") {
                    field.isAccessible = true
                    val inputText = field.get(numberPicker) as? TextView
                    inputText?.typeface = typeface
                }
            }

            for (i in 0 until numberPicker.childCount) {
                val child = numberPicker.getChildAt(i)
                if (child is TextView) {
                    child.typeface = typeface
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFormattedTime(
        hourPicker: NumberPicker, minutePicker: NumberPicker, amPmPicker: NumberPicker
    ): String {
        val amPm = amPmValues[amPmPicker.value] // 오전 또는 오후
        var hour = hourPicker.value
        val minute = minValues[minutePicker.value].toInt()

        // 오전과 오후를 고려하여 24시간 형식으로 변환
        if (amPm == "오후" && hour != 12) {
            hour += 12
        } else if (amPm == "오전" && hour == 12) {
            hour = 0
        }

        return String.format("%02d:%02d:%02d", hour, minute, 0)
    }

    private fun onBoardingNetwork(onBoardingData: OnBoardingData) {

        val call: Call<Void> = ServiceCreator.onBoardingService.sendOnBoardingData(onBoardingData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@TimePicker2Activity, SuccessActivity::class.java)
                    startActivity(intent)
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
