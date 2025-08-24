package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepFiveBinding
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import com.pill_mate.pill_mate_android.util.CustomChip
import com.pill_mate.pill_mate_android.pilledit.view.IntakeTimeBottomSheetFragment

class StepFiveFragment : Fragment() {

    private var _binding: FragmentStepFiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    private var selectedMealUnit: String = ""
    private var selectedMealTime: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFiveBinding.inflate(inflater, container, false)

        // 부모 Fragment에서 presenter 얻기
        val parentFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 현재 등록 진행중 데이터 적용
        val currentSchedule = registrationPresenter.getCurrentSchedule()

        selectedMealUnit = currentSchedule.meal_unit.ifEmpty { getString(R.string.five_meal_unit_after) }

        // 기본값 30분으로: meal_time 이 null 이거나 0 이면 30분으로 설정
        selectedMealTime = currentSchedule.meal_time.takeIf { it != null && it > 0 }
            ?: getString(R.string.five_default_minutes).toInt()

        showSelectedIntake() // "식후 30분" 등 표시

        val selectedTimes = registrationPresenter.getSelectedTimes()
        setSelectedTimes(selectedTimes)

        // 클릭 시 바텀시트로 intake 정보(식전/후+분) 한 번에 선택
        binding.layoutMealUnit.setOnClickListener {
            IntakeTimeBottomSheetFragment.newInstance(
                selectedMealUnit,
                selectedMealTime
            ) { mealUnit, mealTime ->
                selectedMealUnit = mealUnit
                selectedMealTime = mealTime
                registrationPresenter.updateSchedule { s ->
                    s.copy(meal_unit = mealUnit, meal_time = mealTime)
                }
                showSelectedIntake()
                updateNextButtonState()
            }.show(parentFragmentManager, "IntakeTimeBottomSheetFragment")
        }

        updateNextButtonState()
    }

    private fun showSelectedIntake() {
        val unit = selectedMealUnit.ifEmpty { getString(R.string.five_meal_unit_after) }
        val minText =
            if (selectedMealTime == 0) getString(R.string.five_immediately)
            else "${selectedMealTime}${getString(R.string.five_enter_minutes)}"
        binding.tvMealUnit.text = "$unit $minText"
    }

    private fun setSelectedTimes(selectedTimes: List<String>) {
        binding.llSelectedTimes.removeAllViews()

        if (selectedTimes.contains(getString(R.string.time_empty)) ||
            selectedTimes.contains(getString(R.string.time_before_sleep))) {
            val filteredTimes = selectedTimes.filter {
                it in listOf(
                    getString(R.string.time_morning),
                    getString(R.string.time_lunch),
                    getString(R.string.time_dinner)
                )
            }
            filteredTimes.forEach { time ->
                val chip = CustomChip.createChip(requireContext(), time)
                binding.llSelectedTimes.addView(chip)
            }
            val dosingTextView = CustomChip.createDosingTextView(requireContext())
            binding.llSelectedTimes.addView(dosingTextView)
        } else {
            // 아닌 경우도 chip 생성
            selectedTimes.forEach { time ->
                val chip = CustomChip.createChip(requireContext(), time)
                binding.llSelectedTimes.addView(chip)
            }
        }
    }

    private fun updateNextButtonState() {
        val isInputValid = isValidInput()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        return selectedMealUnit.isNotEmpty() && selectedMealTime >= 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}