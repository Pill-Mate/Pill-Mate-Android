package com.example.pill_mate_android

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.databinding.FragmentStepEightBinding
import com.example.pill_mate_android.pillSearch.model.BottomSheetType
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter
import com.example.pill_mate_android.pillSearch.view.CheckBottomSheetFragment

class StepEightFragment : Fragment() {

    private var _binding: FragmentStepEightBinding? = null
    private val binding get() = _binding!!

    private var selectedVolumeUnit: String = "" // 기본값 제거
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepEightBinding.inflate(inflater, container, false)

        // MedicineRegistrationFragment에 연결된 Presenter 가져오기
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVolumeCountEditText()

        // 단위 선택을 위한 BottomSheet 연결
        binding.layoutMedicineUnit.setOnClickListener {
            openBottomSheet()
        }

        // BottomSheet에서 선택한 값을 수신
        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedVolumeUnit = it
                updateVolumeUnit(it)
            }
        }
    }

    // EditText 설정 및 동작 구현
    private fun setupVolumeCountEditText() {

        binding.etMedicineVolume.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString().trim()
                val floatValue = inputText.toFloatOrNull()?.takeIf { it > 0 } // 0 초과하는 값만 허용
                if (floatValue != null) {
                    updateVolumeCount(floatValue) // Presenter에 전달
                }
            }
        })

        // 엔터 키 입력 처리 (포커스 해제 및 키보드 닫기)
        binding.etMedicineVolume.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                binding.etMedicineVolume.clearFocus()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMedicineVolume.windowToken, 0)
    }

    private fun openBottomSheet() {
        val bottomSheet = CheckBottomSheetFragment.newInstance(
            type = BottomSheetType.VOLUME_UNIT,
            selectedOption = selectedVolumeUnit // 기존 선택된 값 전달
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    // 선택한 용량 단위를 UI와 Schedule에 업데이트
    private fun updateVolumeUnit(volumeUnit: String) {
        // UI 업데이트
        binding.tvMedicineUnit.text = volumeUnit

        // Presenter에 업데이트
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(medicine_unit = volumeUnit)
        }
    }

    // 용량 값 업데이트
    private fun updateVolumeCount(volumeCount: Float) {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(medicine_volume = volumeCount)
        }
    }

    fun isValidInput(): Boolean {
        val volumeCountText = binding.etMedicineVolume.text.toString().trim()
        val volumeUnitText = binding.tvMedicineUnit.text.toString().trim()

        // 입력값이 모두 비어있지 않은지 확인
        return volumeCountText.isNotEmpty() && volumeCountText.toFloatOrNull() != null && volumeUnitText.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}