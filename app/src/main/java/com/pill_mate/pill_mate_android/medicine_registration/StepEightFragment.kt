package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.text.Editable
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
            openBottomSheet()
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
                actionId == EditorInfo.IME_ACTION_NEXT ||  // "다음" 버튼 클릭 처리
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etMedicineVolume.clearFocus()
                KeyboardUtil.hideKeyboard(requireContext(), binding.etMedicineVolume) // 키보드 닫기
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
                volumeCountText.toFloatOrNull() != null &&
                volumeCountText.toFloat() > 0.0 &&
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