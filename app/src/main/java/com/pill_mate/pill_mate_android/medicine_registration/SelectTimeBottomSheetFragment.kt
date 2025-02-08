package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R

class SelectTimeBottomSheetFragment(
    private val initiallySelectedTimes: List<String>, // 기존 선택된 시간대
    private val onTimesSelected: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private val selectedTimes = mutableSetOf<String>() // 선택된 시간대 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_bottom_sheet_select_times, container, false)
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timeCheckboxes = mapOf(
            Pair(view.findViewById<CheckBox>(R.id.cb_empty_check), "공복"),
            Pair(view.findViewById<CheckBox>(R.id.cb_morning_check), "아침"),
            Pair(view.findViewById<CheckBox>(R.id.cb_lunch_check), "점심"),
            Pair(view.findViewById<CheckBox>(R.id.cb_dinner_check), "저녁"),
            Pair(view.findViewById<CheckBox>(R.id.cb_before_sleep_check), "취침전")
        )

        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)

        // 기존 선택 상태 초기화
        selectedTimes.clear()
        selectedTimes.addAll(initiallySelectedTimes)

        // 초기 체크 상태 반영 & 체크박스 리스너 설정
        timeCheckboxes.forEach { (checkBox, timeSlot) ->
            checkBox.isChecked = selectedTimes.contains(timeSlot)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTimes.add(timeSlot)
                } else {
                    selectedTimes.remove(timeSlot)
                }

                updateConfirmButton(confirmButton)
            }
        }

        // 확인 버튼 초기 상태 설정
        updateConfirmButton(confirmButton)

        confirmButton.setOnClickListener {
            onTimesSelected(selectedTimes.toList())
            dismiss()
        }
    }

    private fun updateConfirmButton(button: Button) {
        val count = selectedTimes.size
        button.text = getString(R.string.four_selected_count, count)
        button.isEnabled = count > 0
    }
}