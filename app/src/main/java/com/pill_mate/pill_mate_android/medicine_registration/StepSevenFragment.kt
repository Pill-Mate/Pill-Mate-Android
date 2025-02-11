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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepSevenBinding
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import com.pill_mate.pill_mate_android.util.DateConversionUtil
import com.pill_mate.pill_mate_android.util.KeyboardUtil

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

        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedStartDate = currentSchedule.start_date.takeIf { it.isNotEmpty() } ?: DateConversionUtil.getCurrentDate()
        binding.tvStartDate.text = selectedStartDate

        setupDosageDaysEditText()
        setupStartDateSelection()

        updateSchedule()
    }

    private fun setupStartDateSelection() {
        binding.layoutStartDate.setOnClickListener {
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

        binding.etPeriod.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||  // "다음" 버튼 클릭 처리
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etPeriod.clearFocus()
                KeyboardUtil.hideKeyboard(requireContext(), binding.etPeriod) // 키보드 닫기
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun updateEndDateChip() {
        binding.layoutEndDateChip.removeAllViews()

        val endDate = DateConversionUtil.calculateEndDate(selectedStartDate, dosageDays) ?: ""
        val endDateChip = TextView(requireContext()).apply {
            text = getString(R.string.seven_end_date_chip, endDate)
            setPadding(12, 4, 12, 4)
            background = ContextCompat.getDrawable(requireContext(),
                R.drawable.bg_tag_main_blue_2_radius_4
            )
            setTextAppearance(R.style.TagTextStyle)
        }

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

        return periodText.isNotEmpty() &&
                periodText.toIntOrNull()?.let { it > 0 } == true &&
                startDateText.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}