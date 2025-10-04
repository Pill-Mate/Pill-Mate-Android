package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetSelectTimesBinding

class SelectTimeBottomSheetFragment(
    private val initiallySelectedTimes: List<String>, // 기존 선택된 시간대
    private val onTimesSelected: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetSelectTimesBinding? = null
    private val binding get() = _binding!!

    private val selectedTimes = mutableSetOf<String>() // 선택된 시간대 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetSelectTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        selectedTimes.clear()
        selectedTimes.addAll(initiallySelectedTimes)

        val timeSlotViews = listOf(
            Triple(binding.layoutEmpty, binding.cbEmptyCheck, context.getString(R.string.time_empty)),
            Triple(binding.layoutMorning, binding.cbMorningCheck, context.getString(R.string.time_morning)),
            Triple(binding.layoutLunch, binding.cbLunchCheck, context.getString(R.string.time_lunch)),
            Triple(binding.layoutDinner, binding.cbDinnerCheck, context.getString(R.string.time_dinner)),
            Triple(binding.layoutBeforeSleep, binding.cbBeforeSleepCheck, context.getString(R.string.time_before_sleep))
        )

        timeSlotViews.forEach { (layout, checkBox, timeSlot) ->
            checkBox.isChecked = selectedTimes.contains(timeSlot)

            val toggleCheck: (Boolean) -> Unit = { isChecked ->
                checkBox.isChecked = isChecked
                if (isChecked) selectedTimes.add(timeSlot)
                else selectedTimes.remove(timeSlot)
                updateConfirmButton()
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                toggleCheck(isChecked)
            }

            layout.setOnClickListener {
                toggleCheck(!checkBox.isChecked)
            }
        }

        updateConfirmButton()

        binding.btnConfirm.setOnClickListener {
            onTimesSelected(selectedTimes.toList())
            dismiss()
        }
    }

    private fun updateConfirmButton() {
        val count = selectedTimes.size
        binding.btnConfirm.text = getString(R.string.select_times_confirm, count)
        binding.btnConfirm.isEnabled = count > 0
    }
}