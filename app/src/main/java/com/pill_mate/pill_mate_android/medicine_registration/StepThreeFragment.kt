package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.GlobalApplication.Companion.amplitude
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepThreeBinding
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepThreeFragment : Fragment() {

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!

    private val dayOrder = listOf(
        R.string.day_sunday, R.string.day_monday, R.string.day_tuesday,
        R.string.day_wednesday, R.string.day_thursday, R.string.day_friday,
        R.string.day_saturday
    )

    private lateinit var dayOrderMap: Map<String, Int>
    private lateinit var selectedDays: List<String>

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
        // 약물 등록 퍼널 1단계 진입
        amplitude.track(
            "funnel_registration_step_viewed",
            mapOf("step_number" to 3)
        )

        setupDaySelection()

        dayOrderMap = dayOrder.withIndex().associate { getString(it.value) to it.index }

        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedDays = if (currentSchedule.intake_frequency.isNotEmpty()) {
            if (currentSchedule.intake_frequency == getString(R.string.three_everyday)) {
                dayOrder.map { getString(it) }
            } else {
                currentSchedule.intake_frequency.split(", ")
            }
        } else {
            dayOrder.map { getString(it) }
        }

        // UI에 반영
        updateSelectedDaysText(selectedDays)

        // 레포지토리에 저장
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
            sortedDays.size == 7 -> getString(R.string.three_everyday)
            sortedDays.isEmpty() -> ""
            else -> sortedDays.joinToString(", ")
        }
    }

    private fun saveSelectedDays(days: List<String>) {
        val formattedDays = if (days.size == 7) getString(R.string.three_everyday) else days.joinToString(", ")

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(intake_frequency = formattedDays)
        }
    }

    private fun updateNextButtonState() {
        val isInputValid = selectedDays.isNotEmpty()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        return selectedDays.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}