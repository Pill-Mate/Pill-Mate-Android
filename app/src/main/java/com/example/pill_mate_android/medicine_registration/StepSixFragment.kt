package com.example.pill_mate_android.medicine_registration

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
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentStepSixBinding
import com.example.pill_mate_android.medicine_registration.model.BottomSheetType
import com.example.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepSixFragment : Fragment() {

    private var _binding: FragmentStepSixBinding? = null
    private val binding get() = _binding!!

    private var selectedDosageUnit: String = "정(개)" // 기본값 설정
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

        // 기존 선택된 단위를 TextView에 표시
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
            }
        }
    }

    // EditText 설정 및 동작 구현
    private fun setupDosageCountEditText() {
        // 초기값 설정
        binding.etEatCount.setText("1")
        // 초기값을 Presenter에 업데이트
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                eat_unit = selectedDosageUnit,
                eat_count = 1
            )
        }

        binding.etEatCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 필요 시 입력값의 변화를 실시간으로 감지 (현재 비워둠)
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

    fun isValidInput(): Boolean {
        val eatCountText = binding.etEatCount.text.toString().trim()
        val eatUnitText = binding.tvEatUnit.text.toString().trim()

        // 입력값이 모두 비어있지 않은지 확인
        return eatCountText.isNotEmpty() && eatCountText.toIntOrNull() != null && eatUnitText.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}