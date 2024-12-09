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

class StepFourFragment : Fragment() {

    private var _binding: FragmentStepFourBinding? = null
    private val binding get() = _binding!!

    private var selectedTimes: MutableSet<String> = linkedSetOf()
    private val allTimes = listOf("공복", "아침", "점심", "저녁", "식간", "취침전")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimeSelection()
        updateSelectedTimeText() // 초기 텍스트 업데이트
    }

    private fun setupTimeSelection() {
        binding.layoutTime.setOnClickListener {
            val bottomSheet = SelectTimeBottomSheetFragment(selectedTimes.toList()) { newSelectedTimes ->
                selectedTimes.clear()
                selectedTimes.addAll(newSelectedTimes)
                updateSelectedTimes() // 선택된 시간대 업데이트
                updateSelectedTimeText() // tv_day 텍스트 업데이트

                // MedicineRegistrationFragment의 리사이클러뷰 업데이트
                val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (parentFragment is MedicineRegistrationFragment) {
                    val timeData = if (selectedTimes.isNotEmpty()) {
                        getString(R.string.selected_time, selectedTimes.size, selectedTimes.joinToString(", "))
                    } else {
                        getString(R.string.selected_none)
                    }
                    parentFragment.updateRecyclerViewData("횟수", timeData)
                }
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun updateSelectedTimes() {
        binding.llSelectedTimes.removeAllViews() // 기존 태그 제거

        // 선택된 시간대 정렬 후 추가
        allTimes.forEach { time ->
            if (selectedTimes.contains(time)) {
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
    }

    private fun updateSelectedTimeText() {
        binding.tvDay.text = when {
            selectedTimes.isEmpty() -> getString(R.string.enter_time)
            else -> getString(R.string.selected_count, selectedTimes.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}