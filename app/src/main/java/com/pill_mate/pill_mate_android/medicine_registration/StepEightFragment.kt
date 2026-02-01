package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepEightBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import com.pill_mate.pill_mate_android.util.KeyboardUtil
import com.pill_mate.pill_mate_android.util.setMinMaxDecimalValue

class StepEightFragment : Fragment() {

    private var _binding: FragmentStepEightBinding? = null
    private val binding get() = _binding!!

    private var selectedVolumeUnit: String = ""
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepEightBinding.inflate(inflater, container, false)

        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedVolumeUnit = currentSchedule.medicine_unit.takeIf { it.isNotEmpty() } ?: "SKIP"
        if (selectedVolumeUnit != "SKIP") {
            binding.tvMedicineUnit.text = selectedVolumeUnit
        }

        setupVolumeCountEditText()

        binding.layoutMedicineUnit.setOnClickListener {
            // EditText 상태 초기화
            binding.etMedicineVolume.clearFocus()

            openBottomSheet()
        }

        binding.rootLayout.setOnTouchListener { v, _ ->
            binding.etMedicineVolume.clearFocus()
            KeyboardUtil.hideKeyboard(requireContext(), v)
            false  // 터치 이벤트를 계속 전달하기 위해 false 리턴
        }

        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedVolumeUnit = it
                updateVolumeUnit(it)
                updateNextButtonState()
            }
        }
    }

    private fun setupVolumeCountEditText() {
        // 소수점 입력 허용: 0.1 ~ 999.9, 소수점 이하 최대 1자리 허용
        binding.etMedicineVolume.setMinMaxDecimalValue(min = 0.1f, max = 999.9f, decimalLimit = 1)

        // 정수부 3자리 제한 필터 추가 (기존 필터 유지)
        val integerMax3Filter = InputFilter { source, start, end, dest, dstart, dend ->
            val newText =
                dest.substring(0, dstart) +
                        source.subSequence(start, end) +
                        dest.substring(dend)

            // 비어있으면 허용(삭제/초기 입력 과정)
            if (newText.isBlank()) return@InputFilter null

            // 정수부만 3자리 제한 (예: 999.9 OK, 1000 차단)
            val integerPart = newText.substringBefore('.', newText)

            // "."로 시작하는 입력 같은 케이스는 정수부가 ""가 될 수 있으니 허용
            if (integerPart.isNotEmpty() && integerPart.length > 3) "" else null
        }

        val existingFilters = binding.etMedicineVolume.filters ?: emptyArray()
        binding.etMedicineVolume.filters = existingFilters + integerMax3Filter

        binding.etMedicineVolume.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString().trim()
                val floatValue = inputText.toFloatOrNull()

                if (floatValue != null && floatValue > 0.0) { // 0.0보다 클 때만 업데이트
                    updateVolumeCount(floatValue)
                } else {
                    updateVolumeCount(0f) // 잘못된 값 입력 시 기본값 설정
                }
            }
        })

        binding.etMedicineVolume.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                binding.etMedicineVolume.clearFocus()
                KeyboardUtil.hideKeyboard(requireContext(), binding.etMedicineVolume)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun openBottomSheet() {
        val bottomSheet = RadioButtonBottomSheetFragment.newInstance(
            type = BottomSheetType.VOLUME_UNIT,
            selectedOption = selectedVolumeUnit
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun updateVolumeUnit(volumeUnit: String) {
        binding.tvMedicineUnit.text = volumeUnit
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(medicine_unit = volumeUnit)
        }
    }

    private fun updateVolumeCount(volumeCount: Float) {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(medicine_volume = volumeCount)
        }
    }

    private fun updateNextButtonState() {
        val isInputValid = isValidInput()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        val volumeCountText = binding.etMedicineVolume.text.toString().trim()
        val volumeUnitText = binding.tvMedicineUnit.text.toString().trim()

        return volumeCountText.isNotEmpty() &&
                volumeUnitText.isNotEmpty() &&
                volumeUnitText != "SKIP"
    }

    fun saveData() {
        val volume = binding.etMedicineVolume.text.toString().toFloatOrNull() ?: 0f
        val unit = binding.tvMedicineUnit.text.toString().takeIf { it.isNotEmpty() } ?: "SKIP"

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(medicine_volume = volume, medicine_unit = unit)
        }

        // 저장 후 isSkipped 플래그를 false로 설정
        registrationPresenter.isSkipped = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}