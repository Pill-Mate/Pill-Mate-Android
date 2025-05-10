package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.pill_mate.pill_mate_android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DaySelectionBottomSheetFragment(
    private val initiallySelectedDays: List<String> = emptyList(),
    private val onDaysSelected: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var dayOrder: List<String>
    private lateinit var dayOrderMap: Map<String, Int>
    private val selectedDays = mutableSetOf<String>()

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

        dayOrder = listOf(getString(R.string.day_sunday), getString(R.string.day_monday), getString(R.string.day_tuesday),
            getString(R.string.day_wednesday), getString(R.string.day_thursday), getString(R.string.day_friday),
            getString(R.string.day_saturday))

        dayOrderMap = dayOrder.withIndex().associate { it.value to it.index }

        val dayButtons = mapOf(
            view.findViewById<Button>(R.id.btn_sun) to getString(R.string.day_sunday),
            view.findViewById<Button>(R.id.btn_mon) to getString(R.string.day_monday),
            view.findViewById<Button>(R.id.btn_tue) to getString(R.string.day_tuesday),
            view.findViewById<Button>(R.id.btn_wed) to getString(R.string.day_wednesday),
            view.findViewById<Button>(R.id.btn_thu) to getString(R.string.day_thursday),
            view.findViewById<Button>(R.id.btn_fri) to getString(R.string.day_friday),
            view.findViewById<Button>(R.id.btn_sat) to getString(R.string.day_saturday)
        )

        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)

        // 초기 선택 상태 반영
        if (initiallySelectedDays.isEmpty()) {
            selectedDays.addAll(dayOrder) // 모든 요일 기본 선택
        } else {
            selectedDays.clear()
            selectedDays.addAll(initiallySelectedDays)
        }

        // 초기 선택 상태에 맞게 버튼 선택 상태 변경
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

        // 확인 버튼 클릭 시 선택한 요일을 콜백으로 전달
        confirmButton.setOnClickListener {
            val sortedDays = selectedDays.sortedBy { dayOrderMap[it] ?: Int.MAX_VALUE }
            onDaysSelected(sortedDays)
            dismiss()
        }

        // 초기 상태에서 확인 버튼 활성화
        confirmButton.isEnabled = selectedDays.isNotEmpty()
    }
}