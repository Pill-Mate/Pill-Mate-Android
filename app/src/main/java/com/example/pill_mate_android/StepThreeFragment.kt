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

    private var selectedDays = listOf("일", "월", "화", "수", "목", "금", "토") // 초기 선택된 요일 (매일)

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
    }

    private fun setupDaySelection() {
        binding.layoutDay.setOnClickListener {
            val bottomSheetFragment = DaySelectionBottomSheetFragment(selectedDays) { newSelectedDays ->
                selectedDays = newSelectedDays
                updateSelectedDaysText(selectedDays)

                // MedicineRegistrationFragment에 데이터 전달
                (requireActivity() as? MedicineRegistrationFragment)?.updateRecyclerViewData(
                    "요일", // Label
                    if (selectedDays.size == 7) "매일" else selectedDays.joinToString(", ")
                )
            }
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun updateSelectedDaysText(selectedDays: List<String>) {
        binding.tvDay.text = when {
            selectedDays.size == 7 -> "매일" // 모든 요일이 선택된 경우
            selectedDays.isEmpty() -> "" // 아무것도 선택되지 않은 경우 (예외 처리)
            else -> selectedDays.joinToString(", ") // 선택된 요일을 콤마로 구분
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}