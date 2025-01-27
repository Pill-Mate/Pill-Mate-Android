package com.example.pill_mate_android.medicine_registration

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentStepSevenBinding
import com.example.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import java.text.SimpleDateFormat
import java.util.*

class StepSevenFragment : Fragment() {

    private var _binding: FragmentStepSevenBinding? = null
    private val binding get() = _binding!!

    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    private var selectedStartDate: String = "0000.00.00"
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

        binding.tvStartDate.text = selectedStartDate
        setupDosageDaysEditText()
        setupStartDateSelection()

        updateSchedule()
    }

    private fun setupStartDateSelection() {
        binding.layoutStartDate.setOnClickListener {
            val calendarBottomSheet = CalendarBottomSheetFragment.newInstance { selectedDate ->
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

                if (dosageDays > 0 && selectedStartDate != "0000.00.00") {
                    updateEndDateChip()
                } else {
                    binding.layoutEndDateChip.removeAllViews()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPeriod.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                binding.etPeriod.clearFocus()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etPeriod.windowToken, 0)
    }

    private fun updateEndDateChip() {
        binding.layoutEndDateChip.removeAllViews()

        val endDate = calculateEndDate(selectedStartDate, dosageDays)
        val endDateChip = TextView(requireContext()).apply {
            text = "종료일: $endDate"
            setPadding(12, 4, 12, 4)
            background = ContextCompat.getDrawable(requireContext(),
                R.drawable.bg_tag_main_blue_2_radius_4
            )
            setTextAppearance(R.style.TagTextStyle)
        }

        binding.layoutEndDateChip.addView(endDateChip)
    }

    private fun calculateEndDate(startDate: String, days: Int): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val date = dateFormat.parse(startDate) ?: return "0000.00.00"

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, days - 1)

        return dateFormat.format(calendar.time)
    }

    private fun updateSchedule() {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                start_date = selectedStartDate,
                intake_period = dosageDays
            )
        }
    }

    fun isValidInput(): Boolean {
        val periodText = binding.etPeriod.text.toString().trim()
        val startDateText = binding.tvStartDate.text.toString().trim()

        // 입력값이 모두 비어있지 않은지 확인
        return periodText.isNotEmpty() && periodText.toIntOrNull() != null && startDateText.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}