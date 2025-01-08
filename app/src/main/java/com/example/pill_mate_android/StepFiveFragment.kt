package com.example.pill_mate_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.databinding.FragmentStepFiveBinding
import com.example.pill_mate_android.pillSearch.model.BottomSheetType
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter
import com.example.pill_mate_android.pillSearch.view.CheckBottomSheetFragment

class StepFiveFragment : Fragment() {

    private var _binding: FragmentStepFiveBinding? = null
    private val binding get() = _binding!!

    private var selectedMealUnit: String = "식후" // 기본값 설정
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFiveBinding.inflate(inflater, container, false)

        // MedicineRegistrationFragment에 연결된 Presenter 가져오기
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기존 선택된 meal_unit 값을 TextView에 표시
        binding.tvMealUnit.text = selectedMealUnit

        // Presenter에서 기존 selectedTimes 가져오기
        val selectedTimes = registrationPresenter.getSelectedTimes()
        setSelectedTimes(selectedTimes)

        binding.layoutMealUnit.setOnClickListener {
            val type = if (selectedTimes.contains("취침전") || selectedTimes.contains("공복")) {
                BottomSheetType.MEAL_TIME_1
            } else {
                BottomSheetType.MEAL_TIME_0
            }
            openBottomSheet(type)
        }

        setupMealTimeEditText()

        // 바텀시트에서 선택한 값을 수신
        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedMealUnit = it
                updateMealUnit(it)
            }
        }
    }

    // EditText 설정 및 동작 구현
    private fun setupMealTimeEditText() {
        // 초기값 설정
        binding.etMinutes.setText("30분")
        // 초기값을 Presenter에 업데이트
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                meal_unit = selectedMealUnit,
                meal_time = 30
            )
        }

        // 텍스트 변경 중복 방지 플래그
        var isEditing = false

        // 포커스 변경 이벤트 처리
        binding.etMinutes.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 포커스를 얻었을 때 기존 값을 초기화
                binding.etMinutes.setText("분")
                binding.etMinutes.setSelection(binding.etMinutes.text.length - 1) // 커서를 '분' 앞에 위치
            } else {
                // 포커스를 잃었을 때 입력값 처리
                val inputText = binding.etMinutes.text.toString().replace("분", "").trim()
                if (inputText.isNotEmpty()) {
                    binding.etMinutes.setText("${inputText}분")
                } else {
                    binding.etMinutes.setText("30분") // 빈 값일 경우 기본값 복원
                    updateMealTime(30) // 기본값 30 전달
                }
            }
        }

        // 입력값 상태를 실시간으로 감지
        binding.etMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEditing) return // 중복 호출 방지

                val inputText = s.toString().replace("분", "").trim()
                if (inputText.isNotEmpty() && inputText.all { it.isDigit() }) {
                    val intValue = inputText.toIntOrNull()
                    if (intValue != null) {
                        isEditing = true
                        binding.etMinutes.setText("${intValue}분")
                        binding.etMinutes.setSelection(binding.etMinutes.text.length - 1) // 커서를 '분' 앞에 위치
                        isEditing = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString().replace("분", "").trim()
                val intValue = inputText.toIntOrNull()
                if (intValue != null) {
                    updateMealTime(intValue) // 입력값을 Presenter에 전달
                }
            }
        })

        // 엔터 키 입력 처리 (포커스 해제)
        binding.etMinutes.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etMinutes.clearFocus() // 포커스 해제
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun openBottomSheet(type: BottomSheetType) {
        val bottomSheet = CheckBottomSheetFragment.newInstance(
            type = type,
            selectedOption = selectedMealUnit // 기존 선택된 값 전달
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun setSelectedTimes(SelectedTimes: List<String>) {
        binding.llSelectedTimes.removeAllViews()

        // "공복" 또는 "취침전"이 포함되어 있다면 필터링된 시간대만 표시
        if (SelectedTimes.contains("공복") || SelectedTimes.contains("취침전")) {
            val filteredTimes = SelectedTimes.filter { it in listOf("아침", "점심", "저녁") }

            // 필터링된 시간대만 동적 뷰 생성
            filteredTimes.forEach { time ->
                val textView = TextView(requireContext()).apply {
                    text = time
                    setPadding(12, 4, 12, 4)
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tag_main_blue_2_radius_4)
                    setTextAppearance(R.style.TagTextStyle)

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.marginEnd = 6
                    layoutParams = params
                }
                binding.llSelectedTimes.addView(textView)
            }

            val dosingTextView = TextView(requireContext()).apply {
                text = "복용시"
                setPadding(8, 0, 8, 0)
                setTextAppearance(R.style.TagTextStyle) // 필요한 스타일 적용
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2)) // 원하는 색상 적용

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams = params
            }
            binding.llSelectedTimes.addView(dosingTextView)
        }
    }

    //선택한 식사 단위(meal_unit)를 UI와 Schedule에 업데이트
    private fun updateMealUnit(mealUnit: String) {
        // UI 업데이트
        binding.tvMealUnit.text = mealUnit

        // Presenter에 Schedule 업데이트 요청
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(meal_unit = mealUnit)
        }
    }

    private fun updateMealTime(mealTime: Int) {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(meal_time = mealTime)
        }
    }

    fun isValidInput(): Boolean {
        val mealUnit = binding.tvMealUnit.text.toString()
        val mealTime = binding.etMinutes.text.toString().replace("분", "").trim()

        return mealUnit.isNotEmpty() && mealTime.isNotEmpty() && mealTime.toIntOrNull() != null && mealTime.toInt() > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}