package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepThreeBinding
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepThreeFragment : Fragment() {

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!

    private val dayOrder = listOf("일", "월", "화", "수", "목", "금", "토")
    private val dayOrderMap = dayOrder.withIndex().associate { it.value to it.index }

    // ✅ 기본값을 "매일"로 설정
    private var selectedDays = listOf("일", "월", "화", "수", "목", "금", "토")

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

        // ✅ 기본값을 UI에 반영
        updateSelectedDaysText(selectedDays)

        // ✅ 기본값을 레포지토리에 저장
        saveSelectedDays(selectedDays)

        updateNextButtonState()
    }

    private fun setupDaySelection() {
        binding.layoutDay.setOnClickListener {
            val bottomSheetFragment = DaySelectionBottomSheetFragment(selectedDays) { newSelectedDays ->
                selectedDays = newSelectedDays

                val sortedDays = selectedDays.sortedBy { dayOrderMap[it] ?: Int.MAX_VALUE }
                updateSelectedDaysText(sortedDays)
                saveSelectedDays(sortedDays)

                updateNextButtonState()
            }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun updateSelectedDaysText(selectedDays: List<String>) {
        val sortedDays = selectedDays.sortedBy { dayOrderMap[it] ?: Int.MAX_VALUE }
        binding.tvDay.text = when {
            sortedDays.size == 7 -> "매일"
            sortedDays.isEmpty() -> ""
            else -> sortedDays.joinToString(", ")
        }
    }

    // ✅ "매일"을 기본값으로 저장하는 함수
    private fun saveSelectedDays(days: List<String>) {
        val formattedDays = if (days.size == 7) "매일" else days.joinToString(", ")

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(intake_frequency = formattedDays)
        }
    }

    private fun updateNextButtonState() {
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