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
import com.pill_mate.pill_mate_android.GlobalApplication.Companion.amplitude
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepSevenBinding
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import com.pill_mate.pill_mate_android.util.CustomChip
import com.pill_mate.pill_mate_android.util.DateConversionUtil
import com.pill_mate.pill_mate_android.util.KeyboardUtil
import com.pill_mate.pill_mate_android.util.setMinMaxIntegerValue

class StepSevenFragment : Fragment() {

    private var _binding: FragmentStepSevenBinding? = null
    private val binding get() = _binding!!

    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    private lateinit var selectedStartDate: String
    private var dosageDays: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepSevenBinding.inflate(inflater, container, false)

        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 약물 등록 퍼널 7단계 진입
        amplitude.track(
            "funnel_registration_step_viewed",
            mapOf("step_number" to 7)
        )

        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedStartDate = currentSchedule.start_date.takeIf { it.isNotEmpty() } ?: DateConversionUtil.getCurrentDate()
        binding.tvStartDate.text = selectedStartDate

        binding.rootLayout.setOnTouchListener { v, _ ->
            binding.etPeriod.clearFocus()
            KeyboardUtil.hideKeyboard(requireContext(), v)
            false  // 터치 이벤트를 계속 전달하기 위해 false 리턴
        }

        setupDosageDaysEditText()
        setupStartDateSelection()

        updateSchedule()
    }

    private fun setupStartDateSelection() {
        binding.layoutStartDate.setOnClickListener {
            // EditText 상태 초기화
            binding.etPeriod.clearFocus()

            val calendarBottomSheet = CalendarBottomSheetFragment.newInstance(selectedStartDate) { selectedDate ->
                selectedStartDate = selectedDate
                binding.tvStartDate.text = selectedDate

                updateSchedule()

                if (dosageDays > 0) {
                    updateEndDateChip()
                }
            }
            calendarBottomSheet.show(parentFragmentManager, calendarBottomSheet.tag)
        }
    }

    private fun setupDosageDaysEditText() {
        // 숫자 범위 제한 (1 ~ 999)
        binding.etPeriod.setMinMaxIntegerValue(1, 999)

        binding.etPeriod.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString().trim()
                dosageDays = inputText.toIntOrNull() ?: 0

                updateSchedule()

                if (dosageDays > 0) {
                    updateEndDateChip()
                } else {
                    binding.layoutEndDateChip.removeAllViews()
                }
                updateNextButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 기존의 setOnEditorActionListener 코드는 그대로 유지
        binding.etPeriod.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etPeriod.clearFocus()
                KeyboardUtil.hideKeyboard(requireContext(), binding.etPeriod)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun updateEndDateChip() {
        binding.layoutEndDateChip.removeAllViews()

        val endDate = DateConversionUtil.calculateEndDate(selectedStartDate, dosageDays) ?: ""
        val endDateChip = CustomChip.createChip(requireContext(), getString(R.string.seven_end_date_chip, endDate))

        binding.layoutEndDateChip.addView(endDateChip)
    }

    private fun updateSchedule() {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                start_date = selectedStartDate,
                intake_period = dosageDays
            )
        }
    }

    private fun updateNextButtonState() {
        val isInputValid = isValidInput()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        val periodText = binding.etPeriod.text.toString().trim()
        val startDateText = binding.tvStartDate.text.toString().trim()

        return periodText.isNotEmpty() && startDateText.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}