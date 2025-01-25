package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pill_mate_android.databinding.FragmentStepFourBinding
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter

class StepFourFragment : Fragment() {

    private var _binding: FragmentStepFourBinding? = null
    private val binding get() = _binding!!

    // 시간대 순서 고정
    private val timeOrder = listOf("공복", "아침", "점심", "저녁", "취침전")
    private val timeOrderMap = timeOrder.withIndex().associate { it.value to it.index }

    private var selectedTimes: MutableSet<String> = linkedSetOf()
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFourBinding.inflate(inflater, container, false)

        // Presenter는 MedicineRegistrationFragment에서 공유
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTimeSelection()
        updateSelectedTimeText()
    }

    private fun setupTimeSelection() {
        binding.layoutTime.setOnClickListener {
            val bottomSheet = SelectTimeBottomSheetFragment(selectedTimes.toList()) { newSelectedTimes ->
                // 선택한 시간대 업데이트
                selectedTimes.clear()
                selectedTimes.addAll(newSelectedTimes)

                // 시간대를 고정된 순서로 정렬
                val sortedTimes = selectedTimes.sortedBy { timeOrderMap[it] ?: Int.MAX_VALUE }

                // UI 업데이트 (선택한 시간대 태그 생성)
                updateSelectedTimes(sortedTimes)

                // Presenter에 Schedule 데이터를 업데이트 요청
                registrationPresenter.updateSchedule { schedule ->
                    schedule.copy(intake_count = sortedTimes.joinToString(", "))
                }

                // 선택한 시간대의 수에 따라 텍스트 업데이트
                updateSelectedTimeText()
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun updateSelectedTimes(sortedTimes: List<String>) {
        binding.llSelectedTimes.removeAllViews()

        // 모든 선택된 시간대에 대해 동적 뷰 생성
        sortedTimes.forEach { time ->
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
    }

    private fun updateSelectedTimeText() {
        binding.tvDay.text = when {
            selectedTimes.isEmpty() -> getString(R.string.enter_time)
            else -> getString(R.string.selected_count, selectedTimes.size)
        }
    }

    fun shouldMoveToStepFive(): Boolean {
        return selectedTimes.any { it in listOf("아침", "점심", "저녁") }
    }

    fun isValidInput(): Boolean {
        return selectedTimes.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}