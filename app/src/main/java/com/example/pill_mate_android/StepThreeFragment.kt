package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.databinding.FragmentStepThreeBinding

class StepThreeFragment : Fragment() {

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!

    private var selectedDays = listOf("일", "월", "화", "수", "목", "금", "토")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepThreeBinding.inflate(inflater, container, false)
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
                selectedDays = newSelectedDays
                updateSelectedDaysText(selectedDays)

                val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (parentFragment is MedicineRegistrationFragment) {
                    parentFragment.updateRecyclerViewData(
                        "요일",
                        if (selectedDays.size == 7) "매일" else selectedDays.joinToString(", ")
                    )
                }

                updateNextButtonState()
            }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun updateSelectedDaysText(selectedDays: List<String>) {
        binding.tvDay.text = when {
            selectedDays.size == 7 -> getString(R.string.everyday)
            selectedDays.isEmpty() -> ""
            else -> selectedDays.joinToString(", ")
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