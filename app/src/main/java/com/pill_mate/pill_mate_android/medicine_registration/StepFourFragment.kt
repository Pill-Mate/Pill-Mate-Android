package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepFourBinding
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepFourFragment : Fragment() {

    private var _binding: FragmentStepFourBinding? = null
    private val binding get() = _binding!!

    private val timeOrder = listOf(
        R.string.time_empty,
        R.string.time_morning,
        R.string.time_lunch,
        R.string.time_dinner,
        R.string.time_before_sleep
    )

    private lateinit var timeOrderMap: Map<String, Int>
    private lateinit var selectedTimes: MutableSet<String>
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFourBinding.inflate(inflater, container, false)

        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimeSelection()

        timeOrderMap = timeOrder.withIndex().associate { getString(it.value) to it.index }

        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedTimes = if (currentSchedule.intake_count.isNotEmpty()) {
            currentSchedule.intake_count.split(", ").toMutableSet()
        } else {
            mutableSetOf()
        }

        // UI에 반영
        updateSelectedTimes(selectedTimes.toList())
        updateSelectedTimeText()

        // 레포지토리에 저장
        saveSelectedTimes(selectedTimes.toList())
    }

    private fun setupTimeSelection() {
        binding.layoutTime.setOnClickListener {
            val bottomSheet = SelectTimeBottomSheetFragment(selectedTimes.toList()) { newSelectedTimes ->
                selectedTimes.clear()
                selectedTimes.addAll(newSelectedTimes)

                val sortedTimes = selectedTimes.sortedBy { timeOrderMap[it] ?: Int.MAX_VALUE }

                updateSelectedTimes(sortedTimes)
                saveSelectedTimes(sortedTimes)
                updateSelectedTimeText()
                updateNextButtonState()
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun updateSelectedTimes(sortedTimes: List<String>) {
        binding.llSelectedTimes.removeAllViews()

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

    private fun saveSelectedTimes(times: List<String>) {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(intake_count = times.joinToString(", "))
        }
    }

    private fun updateSelectedTimeText() {
        binding.tvTimeCount.text = when {
            selectedTimes.isEmpty() -> getString(R.string.four_et_count)
            else -> getString(R.string.four_selected_time, selectedTimes.size)
        }
    }

    fun shouldMoveToStepFive(): Boolean {
        return selectedTimes.any { it in listOf(
            getString(R.string.time_morning),
            getString(R.string.time_lunch),
            getString(R.string.time_dinner)
        ) }
    }

    private fun updateNextButtonState() {
        val isInputValid = selectedTimes.isNotEmpty()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        return selectedTimes.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}