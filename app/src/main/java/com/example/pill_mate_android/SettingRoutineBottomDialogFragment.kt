package com.example.pill_mate_android

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.pill_mate_android.databinding.FragmentSettingRoutineBottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingRoutineBottomDialogFragment(private val responseRoutine: ResponseRoutine? = null) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentSettingRoutineBottomDialogBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")
    private val minValues = arrayOf("00", "10", "20", "30", "40")
    private val amPmValues = arrayOf("오전", "오후")
    private var currentOpenPicker: Int = 1 // 현재 열려 있는 타임피커 ID, 초기값은 기상 타임피커

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingRoutineBottomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        responseRoutine?.let { updateUI(it) } // 데이터 업데이트
        setupDropDownButtonListeners()
        onCloseButtonClick()
    }

    private fun updateUI(responseRoutine: ResponseRoutine) {
        responseRoutine.wakeupTime?.let {
            setTime(
                it, binding.tvWakeupTime, binding.hrsPicker1, binding.minPicker1, binding.amPmPicker1
            )
        }
        responseRoutine.bedTime?.let {
            setTime(
                it, binding.tvSleepTime, binding.hrsPicker2, binding.minPicker2, binding.amPmPicker2
            )
        }
        responseRoutine.morningTime?.let {
            setTime(
                it, binding.tvBreakfastTime, binding.hrsPicker3, binding.minPicker3, binding.amPmPicker3
            )
        }
        responseRoutine.lunchTime?.let {
            setTime(
                it, binding.tvLunchTime, binding.hrsPicker4, binding.minPicker4, binding.amPmPicker4
            )
        }
        responseRoutine.dinnerTime?.let {
            setTime(
                it, binding.tvDinnerTime, binding.hrsPicker5, binding.minPicker5, binding.amPmPicker5
            )
        }
    }

    private fun setTime(
        time: String, textView: TextView, hrsPicker: NumberPicker, minPicker: NumberPicker, amPmPicker: NumberPicker
    ) {
        val (hour, minute, amPm) = parseTime(time)
        textView.text = "$amPm $hour:${"%02d".format(minute)}"
        hrsPicker.value = hour
        minPicker.value = minute / 10
        amPmPicker.value = if (amPm == "오전") 0 else 1
    }

    private fun parseTime(time: String): Triple<Int, Int, String> {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val amPm = if (hour < 12) "오전" else "오후"
        val adjustedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return Triple(adjustedHour, minute, amPm)
    }

    private fun initView() {
        with(binding) {
            initNumberPicker(hrsPicker1, 1, 12, 8) { updateWakeupTime() }
            initNumberPicker(minPicker1, 0, minValues.size - 1, 0, minValues) { updateWakeupTime() }
            initNumberPicker(amPmPicker1, 0, amPmValues.size - 1, 0, amPmValues) { updateWakeupTime() }

            initNumberPicker(hrsPicker2, 1, 12, 9) { updateSleepTime() }
            initNumberPicker(minPicker2, 0, minValues.size - 1, 0, minValues) { updateSleepTime() }
            initNumberPicker(amPmPicker2, 0, amPmValues.size - 1, 1, amPmValues) { updateSleepTime() }

            initNumberPicker(hrsPicker3, 1, 12, 8) { updateBreakfastTime() }
            initNumberPicker(minPicker3, 0, minValues.size - 1, 0, minValues) { updateBreakfastTime() }
            initNumberPicker(amPmPicker3, 0, amPmValues.size - 1, 0, amPmValues) { updateBreakfastTime() }

            initNumberPicker(hrsPicker4, 1, 12, 8) { updateLunchTime() }
            initNumberPicker(minPicker4, 0, minValues.size - 1, 0, minValues) { updateLunchTime() }
            initNumberPicker(amPmPicker4, 0, amPmValues.size - 1, 0, amPmValues) { updateLunchTime() }

            initNumberPicker(hrsPicker5, 1, 12, 8) { updateDinnerTime() }
            initNumberPicker(minPicker5, 0, minValues.size - 1, 0, minValues) { updateDinnerTime() }
            initNumberPicker(amPmPicker5, 0, amPmValues.size - 1, 0, amPmValues) { updateDinnerTime() }

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

            customizeNumberPicker(hrsPicker4, fontResId)
            customizeNumberPicker(minPicker4, fontResId)
            customizeNumberPicker(amPmPicker4, fontResId)

            customizeNumberPicker(hrsPicker5, fontResId)
            customizeNumberPicker(minPicker5, fontResId)
            customizeNumberPicker(amPmPicker5, fontResId)

            setTimePickerVisibility(1) // 초기 상태 설정: 기상 타임피커 열기
        }
    }

    private fun setupDropDownButtonListeners() {
        binding.btnDropdown1.setOnClickListener { togglePickerVisibility(1) }
        binding.btnDropdown2.setOnClickListener { togglePickerVisibility(2) }
        binding.btnDropdown3.setOnClickListener { togglePickerVisibility(3) }
        binding.btnDropdown4.setOnClickListener { togglePickerVisibility(4) }
        binding.btnDropdown5.setOnClickListener { togglePickerVisibility(5) }
    }

    private fun togglePickerVisibility(pickerId: Int) {
        if (currentOpenPicker == pickerId) { // 현재 열려 있는 토글을 닫고 다음 토글 자동으로 열기
            val nextPickerId = if (pickerId == 5) 1 else pickerId + 1
            setTimePickerVisibility(nextPickerId)
        } else { // 특정 토글 클릭 시 해당 토글 열기
            setTimePickerVisibility(pickerId)
        }
    }

    private fun setTimePickerVisibility(pickerId: Int) {
        with(binding) {
            showWakeUpTimePicker(pickerId == 1)
            showSleepTimePicker(pickerId == 2)
            showBreakfastTimePicker(pickerId == 3)
            showLunchTimePicker(pickerId == 4)
            showDinnerTimePicker(pickerId == 5)
        }
        currentOpenPicker = pickerId // 현재 열려 있는 타임피커 업데이트
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
            line2.visibility = if (show) View.GONE else View.VISIBLE
            btnDropdown2.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showBreakfastTimePicker(show: Boolean) {
        with(binding) {
            layoutTpBreakfast.visibility = if (show) View.VISIBLE else View.GONE
            line3.visibility = if (show) View.GONE else View.VISIBLE
            btnDropdown3.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showLunchTimePicker(show: Boolean) {
        with(binding) {
            layoutTpLunch.visibility = if (show) View.VISIBLE else View.GONE
            line4.visibility = if (show) View.GONE else View.VISIBLE
            btnDropdown4.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showDinnerTimePicker(show: Boolean) {
        with(binding) {
            layoutTpDinner.visibility = if (show) View.VISIBLE else View.GONE
            btnDropdown5.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            bottomSheet?.background = null
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

    private fun updateBreakfastTime() {
        val amPm = amPmValues[binding.amPmPicker3.value]
        val hour = binding.hrsPicker3.value
        val min = minValues[binding.minPicker3.value]
        binding.tvBreakfastTime.text = "$amPm $hour:$min"
    }

    private fun updateLunchTime() {
        val amPm = amPmValues[binding.amPmPicker4.value]
        val hour = binding.hrsPicker4.value
        val min = minValues[binding.minPicker4.value]
        binding.tvLunchTime.text = "$amPm $hour:$min"
    }

    private fun updateDinnerTime() {
        val amPm = amPmValues[binding.amPmPicker5.value]
        val hour = binding.hrsPicker5.value
        val min = minValues[binding.minPicker5.value]
        binding.tvDinnerTime.text = "$amPm $hour:$min"
    }

    private fun onCloseButtonClick() {
        binding.btnClose.setOnClickListener {
            dismiss()
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
}