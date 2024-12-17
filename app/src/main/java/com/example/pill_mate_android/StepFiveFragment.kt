package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.databinding.FragmentStepFiveBinding
import com.example.pill_mate_android.pillSearch.model.BottomSheetType
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter
import com.example.pill_mate_android.pillSearch.view.CheckBottomSheetFragment

class StepFiveFragment : Fragment() {

    private var _binding: FragmentStepFiveBinding? = null
    private val binding get() = _binding!!

    private var selectedMealTime: String = "식후" // 기본값 설정
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFiveBinding.inflate(inflater, container, false)

        // MedicineRegistrationFragment에 연결된 Presenter 가져오기
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기존 선택된 meal_unit 값을 TextView에 표시
        binding.tvMealUnit.text = selectedMealTime

        binding.layoutMeal.setOnClickListener {
            openMealTimeBottomSheet()
        }

        // 바텀시트에서 선택한 값을 수신
        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedMealTime = it
                updateMealTime(it)
            }
        }
    }

    private fun openMealTimeBottomSheet() {
        val bottomSheet = CheckBottomSheetFragment.newInstance(
            type = BottomSheetType.MEAL_TIME,
            selectedOption = selectedMealTime // 기존 선택된 값 전달
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    //선택한 식사 단위(meal_unit)를 UI와 Schedule에 업데이트
    private fun updateMealTime(mealTime: String) {
        // UI 업데이트
        binding.tvMealUnit.text = mealTime

        // Presenter에 Schedule 업데이트 요청
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(meal_unit = mealTime)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}