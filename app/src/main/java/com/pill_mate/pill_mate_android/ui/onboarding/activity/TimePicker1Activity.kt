package com.pill_mate.pill_mate_android.ui.onboarding.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ActivityTimePicker1Binding

class TimePicker1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTimePicker1Binding
    private var isWakeUpTimePickerVisible = true
    private var isSleepTimePickerVisible = false
    private val minValues = arrayOf("00", "10", "20", "30", "40", "50")
    private val amPmValues = arrayOf("오전", "오후")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimePicker1Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.timepicker1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        onInitButtonClick()
        onNextButtonClick()
        onBackButtonClick()

    }

    private fun initView() { // NumberPicker 초기화
        with(binding) {
            initNumberPicker(hrsPicker1, 1, 12, 8) { updateWakeupTime() }
            initNumberPicker(minPicker1, 0, minValues.size - 1, 0, minValues) { updateWakeupTime() }
            initNumberPicker(amPmPicker1, 0, amPmValues.size - 1, 0, amPmValues) { updateWakeupTime() }

            initNumberPicker(hrsPicker2, 1, 12, 9) { updateSleepTime() }
            initNumberPicker(minPicker2, 0, minValues.size - 1, 0, minValues) { updateSleepTime() }
            initNumberPicker(amPmPicker2, 0, amPmValues.size - 1, 1, amPmValues) { updateSleepTime() }

            val fontResId = R.font.pretendard_bold // NumberPicker 커스터마이징
            customizeNumberPicker(hrsPicker1, fontResId)
            customizeNumberPicker(minPicker1, fontResId)
            customizeNumberPicker(amPmPicker1, fontResId)
            customizeNumberPicker(hrsPicker2, fontResId)
            customizeNumberPicker(minPicker2, fontResId)
            customizeNumberPicker(amPmPicker2, fontResId)
        }

        // 드롭다운 초기 상태 설정
        setTimePickerVisibility(isWakeUpTimePickerVisible, isSleepTimePickerVisible)
    }

    private fun onInitButtonClick() { // 기상 시간 드롭다운 버튼 클릭 시
        binding.btnDropdown1.setOnClickListener {
            isWakeUpTimePickerVisible = !isWakeUpTimePickerVisible
            setTimePickerVisibility(isWakeUpTimePickerVisible, !isWakeUpTimePickerVisible)
        }

        // 취침 시간 드롭다운 버튼 클릭 시
        binding.btnDropdown2.setOnClickListener {
            isSleepTimePickerVisible = !isSleepTimePickerVisible
            setTimePickerVisibility(isSleepTimePickerVisible, !isSleepTimePickerVisible)
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

    private fun setTimePickerVisibility(showWakeUp: Boolean, showSleep: Boolean) {
        showWakeUpTimePicker(showWakeUp)
        showSleepTimePicker(showSleep)
    }

    private fun showWakeUpTimePicker(show: Boolean) {
        with(binding) {
            layoutTpWakeup.visibility = if (show) View.VISIBLE else View.GONE
            line1.visibility = if (show) View.GONE else View.VISIBLE
            btnDropdown1.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showSleepTimePicker(show: Boolean) {
        with(binding) {
            layoutTpSleep.visibility = if (show) View.VISIBLE else View.GONE
            btnDropdown2.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun updateWakeupTime() {
        val amPm = amPmValues[binding.amPmPicker1.value]
        val hour = binding.hrsPicker1.value
        val min = minValues[binding.minPicker1.value]
        binding.tvWakeupTime.text = "$amPm $hour:$min"
    }

    private fun updateSleepTime() {
        val amPm = amPmValues[binding.amPmPicker2.value]
        val hour = binding.hrsPicker2.value
        val min = minValues[binding.minPicker2.value]
        binding.tvSleepTime.text = "$amPm $hour:$min"
    }

    private fun onNextButtonClick() {
        binding.btnNext.setOnClickListener {
            val wakeupTime = getFormattedTime(binding.hrsPicker1, binding.minPicker1, binding.amPmPicker1)
            val bedTime = getFormattedTime(binding.hrsPicker2, binding.minPicker2, binding.amPmPicker2)

            val intent = Intent(this@TimePicker1Activity, TimePicker2Activity::class.java).apply {
                putExtra("WAKEUP_TIME", wakeupTime)
                putExtra("BED_TIME", bedTime)
                val alarmMarketing = intent.getBooleanExtra("ALARM_MARKETING", false)
                putExtra("ALARM_MARKETING", alarmMarketing)
                val alarmInfo = intent.getBooleanExtra("ALARM_INFO", false)
                putExtra("ALARM_INFO", alarmInfo)
            }

            startActivity(intent)

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

}