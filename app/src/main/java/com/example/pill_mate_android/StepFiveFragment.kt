package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.databinding.FragmentStepFiveBinding
import com.example.pill_mate_android.pillSearch.model.BottomSheetType
import com.example.pill_mate_android.pillSearch.view.CheckBottomSheetFragment

class StepFiveFragment : Fragment() {

    private var _binding: FragmentStepFiveBinding? = null
    private val binding get() = _binding!!

    private var selectedMealTime: String = "식후" // 기본값 설정

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 기존 선택된 값을 TextView에 표시
        binding.tvMealUnit.text = selectedMealTime

        // 🔥 layout_meal 클릭 리스너
        binding.layoutMeal.setOnClickListener {
            openMealTimeBottomSheet()
        }

        // 🔥 바텀시트에서 선택한 값을 수신 (key를 "selectedOptionKey"로 통일)
        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedMealTime = it
                binding.tvMealUnit.text = selectedMealTime // 🔥 선택한 값을 tvMealUnit에 반영
            }
        }
    }

    private fun openMealTimeBottomSheet() {
        val bottomSheet = CheckBottomSheetFragment.newInstance(
            type = BottomSheetType.MEAL_TIME,
            selectedOption = selectedMealTime // 🔥 기존 선택된 값 전달
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}