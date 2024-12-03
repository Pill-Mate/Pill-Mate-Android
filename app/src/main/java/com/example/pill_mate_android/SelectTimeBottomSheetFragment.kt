package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectTimeBottomSheetFragment(
    private val initiallySelectedTimes: List<String>, // 기존 선택된 시간대
    private val onTimesSelected: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private val selectedTimes = mutableSetOf<String>() // 선택된 시간대

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_select_times, container, false)
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timeButtons = mapOf(
            Pair(view.findViewById<ImageView>(R.id.iv_fasting_check), "공복"),
            Pair(view.findViewById<ImageView>(R.id.iv_morning_check), "아침"),
            Pair(view.findViewById<ImageView>(R.id.iv_lunch_check), "점심"),
            Pair(view.findViewById<ImageView>(R.id.iv_dinner_check), "저녁"),
            Pair(view.findViewById<ImageView>(R.id.iv_snack_check), "식간"),
            Pair(view.findViewById<ImageView>(R.id.iv_before_sleep_check), "취침전")
        )

        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)

        // 기존 선택 상태 초기화
        selectedTimes.clear()
        selectedTimes.addAll(initiallySelectedTimes)

        timeButtons.forEach { (imageView, timeSlot) ->
            // 초기 선택 상태 반영
            imageView.setImageResource(
                if (selectedTimes.contains(timeSlot)) R.drawable.ic_btn_select else R.drawable.ic_btn_unselect
            )

            imageView.setOnClickListener {
                if (selectedTimes.contains(timeSlot)) {
                    selectedTimes.remove(timeSlot)
                    imageView.setImageResource(R.drawable.ic_btn_unselect)
                } else {
                    selectedTimes.add(timeSlot)
                    imageView.setImageResource(R.drawable.ic_btn_select)
                }

                updateConfirmButton(confirmButton)
            }
        }

        // 확인 버튼 초기 활성화 상태 및 텍스트 설정
        updateConfirmButton(confirmButton)

        confirmButton.setOnClickListener {
            onTimesSelected(selectedTimes.toList())
            dismiss()
        }
    }

    private fun updateConfirmButton(button: Button) {
        val count = selectedTimes.size
        button.text = getString(R.string.selected_count, count)
        button.isEnabled = count > 0
    }
}