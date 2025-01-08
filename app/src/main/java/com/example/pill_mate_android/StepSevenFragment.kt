package com.example.pill_mate_android

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
import com.example.pill_mate_android.databinding.FragmentStepSevenBinding
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter
import com.example.pill_mate_android.pillSearch.view.CalendarBottomSheetFragment
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

        // MedicineRegistrationFragment에 연결된 Presenter 가져오기
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화
        binding.tvStartDate.text = selectedStartDate
        setupDosageDaysEditText()
        setupStartDateSelection()

        // 초기 Presenter 업데이트
        updateSchedule()
    }

    private fun setupStartDateSelection() {
        binding.layoutStartDate.setOnClickListener {
            val calendarBottomSheet = CalendarBottomSheetFragment.newInstance { selectedDate ->
                selectedStartDate = selectedDate
                binding.tvStartDate.text = selectedDate

                // Presenter에 시작일 업데이트
                updateSchedule()

                // 복약일수가 이미 입력되었을 경우 종료일 업데이트
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

                // Presenter에 복약일수 업데이트
                updateSchedule()

                // 종료일 업데이트
                if (dosageDays > 0 && selectedStartDate != "0000.00.00") {
                    updateEndDateChip()
                } else {
                    binding.layoutEndDateChip.removeAllViews() // 종료일 칩 제거
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 엔터 키 입력 처리 (포커스 해제)
        binding.etPeriod.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etPeriod.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun updateEndDateChip() {
        binding.layoutEndDateChip.removeAllViews()

        val endDate = calculateEndDate(selectedStartDate, dosageDays)
        val endDateChip = TextView(requireContext()).apply {
            text = "종료일: $endDate"
            setPadding(12, 4, 12, 4)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tag_main_blue_2_radius_4)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}