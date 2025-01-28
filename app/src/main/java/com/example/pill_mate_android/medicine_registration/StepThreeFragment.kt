package com.example.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentStepThreeBinding
import com.example.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepThreeFragment : Fragment() {

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!

    // 요일 순서 고정 (일 > 월 > 화 > 수 > 목 > 금 > 토)
    private val dayOrder = listOf("일", "월", "화", "수", "목", "금", "토")
    private val dayOrderMap = dayOrder.withIndex().associate { it.value to it.index }

    private var selectedDays = listOf<String>()
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepThreeBinding.inflate(inflater, container, false)

        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDaySelection()
        updateNextButtonState()
    }

    private fun setupDaySelection() {
        binding.layoutDay.setOnClickListener {
            val bottomSheetFragment = DaySelectionBottomSheetFragment(selectedDays) { newSelectedDays ->
                // 사용자가 선택한 요일을 업데이트
                selectedDays = newSelectedDays

                // 요일을 정렬합니다.
                val sortedDays = selectedDays.sortedBy { dayOrderMap[it] ?: Int.MAX_VALUE }

                // 선택한 요일을 TextView에 반영
                updateSelectedDaysText(sortedDays)

                // Presenter에 Schedule 데이터를 업데이트 요청
                registrationPresenter.updateSchedule { schedule ->
                    schedule.copy(
                        intake_frequency = if (sortedDays.size == 7) "매일" else sortedDays.joinToString(", ")
                    )
                }

                // 다음 버튼 활성화/비활성화 상태를 갱신합니다.
                updateNextButtonState()
            }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun updateSelectedDaysText(selectedDays: List<String>) {
        // 선택된 요일을 고정된 순서로 정렬합니다.
        val sortedDays = selectedDays.sortedBy { dayOrderMap[it] ?: Int.MAX_VALUE }

        // TextView에 표시할 내용을 결정합니다.
        binding.tvDay.text = when {
            sortedDays.size == 7 -> "매일"
            sortedDays.isEmpty() -> ""
            else -> sortedDays.joinToString(", ")
        }
    }

    private fun updateNextButtonState() {
        // 선택된 요일이 하나 이상일 때만 다음 버튼이 활성화
        val isInputValid = selectedDays.isNotEmpty()
        (requireActivity() as? MedicineRegistrationFragment)?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        return selectedDays.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}