package com.pill_mate.pill_mate_android.pilledit.view

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetIntakeTimeBinding

class IntakeTimeBottomSheetFragment(
    private val initialMealUnit: String?,
    private val initialMealTime: Int,
    private val onMealTimeSelected: (String, Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetIntakeTimeBinding? = null
    private val binding get() = _binding!!

    // 선택 가능한 시간 옵션 (서버에 보낼 값)
    private val timeOptions = arrayOf("즉시", "10 분", "20 분", "30 분") // UI 표시
    private val timeValues = arrayOf(0, 10, 20, 30) // 서버에 보낼 값
    private val fontResId = R.font.pretendard_bold

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetIntakeTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPickers()

        binding.btnFinish.setOnClickListener {
            val selectedMealUnit = binding.npMealtime.displayedValues[binding.npMealtime.value]
            val selectedMealTime = timeValues[binding.npMinutes.value] // 변환된 숫자 값 가져오기

            onMealTimeSelected(selectedMealUnit, selectedMealTime)
            dismiss()
        }
    }

    private fun setupNumberPickers() { // 식전/즉시/식후 선택
        val mealTimeOptions = arrayOf("식전", "식후")
        binding.npMealtime.apply {
            minValue = 0
            maxValue = mealTimeOptions.size - 1
            displayedValues = mealTimeOptions
            wrapSelectorWheel = false

            // 기존 값 설정
            value = mealTimeOptions.indexOf(initialMealUnit).takeIf { it >= 0 } ?: 0

            // NumberPicker 커스터마이징
            customizeNumberPicker(this, fontResId)
        }

        // "즉시" ~ "30분" 선택 (즉시를 0으로 변환)
        binding.npMinutes.apply {
            minValue = 0
            maxValue = timeOptions.size - 1
            displayedValues = timeOptions // "즉시", "10", "20", "30" 표시
            wrapSelectorWheel = false

            // 기존 값이 timeValues 배열에 있으면 해당 index 설정, 없으면 0(즉시)
            value = timeValues.indexOf(initialMealTime).takeIf { it >= 0 } ?: 0

            // NumberPicker 커스터마이징
            customizeNumberPicker(this, fontResId)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            initialMealUnit: String?, initialMealTime: Int, onMealTimeSelected: (String, Int) -> Unit
        ): IntakeTimeBottomSheetFragment {
            return IntakeTimeBottomSheetFragment(initialMealUnit, initialMealTime, onMealTimeSelected)
        }
    }
}