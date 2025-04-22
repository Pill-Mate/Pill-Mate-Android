package com.pill_mate.pill_mate_android.pilledit.view

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentAlarmTimeBottomSheetBinding
import com.pill_mate.pill_mate_android.util.expandTouchArea

class AlarmTimeBottomSheetFragment(
    private val wakeupTime: String,
    private val bedTime: String,
    private val onTimeSelected: (String, String) -> Unit,
    private val showFastingPicker: Boolean,
    private val showBedtimePicker: Boolean
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAlarmTimeBottomSheetBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")
    private val minValues = arrayOf("00", "10", "20", "30", "40", "50")
    private val amPmValues = arrayOf("오전", "오후")
    private var currentOpenPicker: Int = 1 // 현재 열려 있는 타임피커 ID, 초기값은 기상 타임피커

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmTimeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("AlarmTimeBottomSheetFragment", "Received wakeupTime: $wakeupTime, bedTime: $bedTime")
        Log.d(
            "AlarmTimeBottomSheetFragment",
            "showFastingPicker: $showFastingPicker, showBedtimePicker: $showBedtimePicker"
        )

        initView()
        updateUI()

        setupDropDownButtonListeners()
        onDoneButtonClick()
    }

    private fun updateUI() {
        if (showFastingPicker) setTime(
            wakeupTime, binding.tvFastingTime, binding.hrsPicker1, binding.minPicker1, binding.amPmPicker1
        )
        if (showBedtimePicker) setTime(
            bedTime, binding.tvBeforeSleepTime, binding.hrsPicker2, binding.minPicker2, binding.amPmPicker2
        )
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

        binding.layoutTvFasting.visibility = if (showFastingPicker) View.VISIBLE else View.GONE
        binding.layoutTpFasting.visibility = if (showFastingPicker) View.VISIBLE else View.GONE
        binding.layoutTvBeforeSleep.visibility = if (showBedtimePicker) View.VISIBLE else View.GONE
        binding.layoutTpBeforeSleep.visibility = if (showBedtimePicker) View.VISIBLE else View.GONE

        setTimePickerVisibility(
            when {
                showFastingPicker && !showBedtimePicker -> 1
                showBedtimePicker && !showFastingPicker -> 2
                else -> 1 // default
            }
        )

        binding.btnDropdown1.post {
            binding.btnDropdown1.expandTouchArea(200) // 200dp 만큼 터치 영역 확장
        }

        binding.btnDropdown2.post {
            binding.btnDropdown2.expandTouchArea(200)
        }

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
    }

    private fun setupDropDownButtonListeners() {
        binding.btnDropdown1.setOnClickListener { togglePickerVisibility(1) }
        binding.btnDropdown2.setOnClickListener { togglePickerVisibility(2) }
    }

    private fun togglePickerVisibility(pickerId: Int) {
        if (currentOpenPicker == pickerId) { // 현재 열려 있는 피커에서 토글한 경우
            val nextPickerId = when (pickerId) {
                1 -> if (showBedtimePicker) 2 else 1
                2 -> if (showFastingPicker) 1 else 2
                else -> pickerId
            }

            if (nextPickerId != pickerId) {
                setTimePickerVisibility(nextPickerId)
            }
        } else {
            setTimePickerVisibility(pickerId)
        }
    }

    private fun setTimePickerVisibility(pickerId: Int) {
        showWakeUpTimePicker(pickerId == 1)
        showSleepTimePicker(pickerId == 2)
        updateLineVisibility(pickerId)
        currentOpenPicker = pickerId
    }

    private fun updateLineVisibility(pickerId: Int) {
        binding.line1.visibility =
            if (showFastingPicker && showBedtimePicker && pickerId == 2) View.VISIBLE else View.GONE
    }

    private fun showWakeUpTimePicker(show: Boolean) {
        with(binding) {
            layoutTpFasting.visibility = if (show) View.VISIBLE else View.GONE
            btnDropdown1.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    private fun showSleepTimePicker(show: Boolean) {
        with(binding) {
            layoutTpBeforeSleep.visibility = if (show) View.VISIBLE else View.GONE
            btnDropdown2.setImageResource(if (show) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up)
        }
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            bottomSheet.background = null
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
        binding.tvFastingTime.text = "$amPm $hour:$min"
    }

    private fun updateSleepTime() {
        val amPm = amPmValues[binding.amPmPicker2.value]
        val hour = binding.hrsPicker2.value
        val min = minValues[binding.minPicker2.value]
        binding.tvBeforeSleepTime.text = "$amPm $hour:$min"
    }

    private fun onDoneButtonClick() {
        binding.btnDone.setOnClickListener {
            val selectedWakeupTime = getFormattedTime(binding.hrsPicker1, binding.minPicker1, binding.amPmPicker1)
            val selectedBedTime = getFormattedTime(binding.hrsPicker2, binding.minPicker2, binding.amPmPicker2)

            onTimeSelected(selectedWakeupTime, selectedBedTime)
            dismiss() // 바텀시트 닫기
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