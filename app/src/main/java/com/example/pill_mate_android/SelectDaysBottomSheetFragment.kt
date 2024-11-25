package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DaySelectionBottomSheetFragment(
    private val initiallySelectedDays: List<String>, // 초기 선택된 요일
    private val onDaysSelected: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private val selectedDays = mutableSetOf<String>() // 선택된 요일

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_select_days, container, false)
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dayButtons = mapOf(
            view.findViewById<Button>(R.id.btn_sun) to "일",
            view.findViewById<Button>(R.id.btn_mon) to "월",
            view.findViewById<Button>(R.id.btn_tue) to "화",
            view.findViewById<Button>(R.id.btn_wed) to "수",
            view.findViewById<Button>(R.id.btn_thu) to "목",
            view.findViewById<Button>(R.id.btn_fri) to "금",
            view.findViewById<Button>(R.id.btn_sat) to "토"
        )

        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)

        // 초기 선택 상태 반영
        selectedDays.clear()
        selectedDays.addAll(initiallySelectedDays)

        dayButtons.forEach { (button, day) ->
            button.isSelected = selectedDays.contains(day)
        }

        // 버튼 클릭 시 선택 상태 변경
        dayButtons.forEach { (button, day) ->
            button.setOnClickListener {
                button.isSelected = !button.isSelected
                if (button.isSelected) {
                    selectedDays.add(day)
                } else {
                    selectedDays.remove(day)
                }

                confirmButton.isEnabled = selectedDays.isNotEmpty()
            }
        }

        // 확인 버튼 클릭 처리
        confirmButton.setOnClickListener {
            onDaysSelected(selectedDays.toList())
            dismiss()
        }

        // 초기 상태에서 확인 버튼 활성화
        confirmButton.isEnabled = selectedDays.isNotEmpty()
    }
}