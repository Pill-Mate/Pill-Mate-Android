package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepSixBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepSixFragment : Fragment() {

    private var _binding: FragmentStepSixBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedDosageUnit: String
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepSixBinding.inflate(inflater, container, false)

        // MedicineRegistrationFragment에 연결된 Presenter 가져오기
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedDosageUnit = currentSchedule.eat_unit.ifEmpty { getString(R.string.dosage_unit_tablet) }
        binding.tvEatUnit.text = selectedDosageUnit

        setupDosageCountEditText()

        binding.layoutEatUnit.setOnClickListener {
            openBottomSheet()
        }

        // 바텀시트에서 선택한 값을 수신
        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedDosageUnit = it
                updateDosageUnit(it)
                updateNextButtonState()
            }
        }
        updateNextButtonState()
    }

    // EditText 설정 및 동작 구현
    private fun setupDosageCountEditText() {
        // 초기값 설정
        binding.etEatCount.setText(getString(R.string.six_default_count))
        // 초기값을 Presenter에 업데이트
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                eat_unit = selectedDosageUnit,
                eat_count = getString(R.string.six_default_count).toInt()
            )
        }

        binding.etEatCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString().trim()
                val intValue = inputText.toIntOrNull()?.takeIf { it > 0 } // 0 초과하는 값만 허용
                if (intValue != null) {
                    updateDosageCount(intValue) // Presenter에 전달
                }
            }
        })

        // 엔터 키 입력 처리 (포커스 해제)
        binding.etEatCount.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etEatCount.clearFocus() // 포커스 해제
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun openBottomSheet() {
        val bottomSheet = CheckBottomSheetFragment.newInstance(
            type = BottomSheetType.DOSAGE_UNIT,
            selectedOption = selectedDosageUnit // 기존 선택된 값 전달
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    // 선택한 투약 단위를 UI와 Schedule에 업데이트
    private fun updateDosageUnit(dosageUnit: String) {
        // UI 업데이트
        binding.tvEatUnit.text = dosageUnit

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(eat_unit = dosageUnit)
        }
    }

    private fun updateDosageCount(dosageCount: Int) {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(eat_count = dosageCount)
        }
    }

    private fun updateNextButtonState() {
        val isInputValid = isValidInput()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        val eatCountText = binding.etEatCount.text.toString().trim()
        val eatUnitText = binding.tvEatUnit.text.toString().trim()
        val eatCount = eatCountText.toIntOrNull()

        return eatCount != null && eatCount > 0 && eatUnitText.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}