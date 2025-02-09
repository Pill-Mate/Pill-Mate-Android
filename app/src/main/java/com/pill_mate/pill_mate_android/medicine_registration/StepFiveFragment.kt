package com.pill_mate.pill_mate_android.medicine_registration

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepFiveBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter

class StepFiveFragment : Fragment() {

    private var _binding: FragmentStepFiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedMealUnit: String
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepFiveBinding.inflate(inflater, container, false)

        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentSchedule = registrationPresenter.getCurrentSchedule()
        selectedMealUnit = currentSchedule.meal_unit.ifEmpty { getString(R.string.five_meal_unit_after) }
        binding.tvMealUnit.text = selectedMealUnit

        val selectedTimes = registrationPresenter.getSelectedTimes()
        setSelectedTimes(selectedTimes)

        binding.layoutMealUnit.setOnClickListener {
            openBottomSheet()
        }

        setupMealTimeEditText()

        parentFragmentManager.setFragmentResultListener("selectedOptionKey", viewLifecycleOwner) { _, bundle ->
            val selectedOption = bundle.getString("selectedOption")
            selectedOption?.let {
                selectedMealUnit = it
                updateMealUnit(it)
            }
        }
        updateNextButtonState()
    }

    private fun setupMealTimeEditText() {
        binding.etMinutes.setText(getString(R.string.five_default_minutes) + getString(R.string.five_enter_minutes))
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                meal_unit = selectedMealUnit,
                meal_time = getString(R.string.five_default_minutes).toInt()
            )
        }

        var isEditing = false

        binding.etMinutes.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etMinutes.setText(getString(R.string.five_enter_minutes))
                binding.etMinutes.setSelection(binding.etMinutes.text.length - 1)
            } else {
                val inputText = binding.etMinutes.text.toString().replace(getString(R.string.five_enter_minutes), "").trim()
                if (inputText.isNotEmpty()) {
                    binding.etMinutes.setText("${inputText}${getString(R.string.five_enter_minutes)}")
                } else {
                    binding.etMinutes.setText("${getString(R.string.five_default_minutes)}${getString(R.string.five_enter_minutes)}")
                    updateMealTime(getString(R.string.five_default_minutes).toInt())
                }
            }
        }

        binding.etMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isEditing) return

                val inputText = s.toString().replace(getString(R.string.five_enter_minutes), "").trim()
                if (inputText.isNotEmpty() && inputText.all { it.isDigit() }) {
                    val intValue = inputText.toIntOrNull()
                    if (intValue != null) {
                        isEditing = true
                        binding.etMinutes.setText("${intValue}${getString(R.string.five_enter_minutes)}")
                        binding.etMinutes.setSelection(binding.etMinutes.text.length - 1)
                        isEditing = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString().replace(getString(R.string.five_enter_minutes), "").trim()
                val intValue = inputText.toIntOrNull()
                if (intValue != null) {
                    updateMealTime(intValue)
                }
            }
        })

        binding.etMinutes.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.etMinutes.clearFocus()
                hideKeyboard()  // 키보드 닫기 추가
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun openBottomSheet() {
        val bottomSheet = CheckBottomSheetFragment.newInstance(
            type = BottomSheetType.MEAL_TIME,
            selectedOption = selectedMealUnit
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun setSelectedTimes(SelectedTimes: List<String>) {
        binding.llSelectedTimes.removeAllViews()

        if (SelectedTimes.contains(getString(R.string.time_empty)) || SelectedTimes.contains(getString(R.string.time_before_sleep))) {
            val filteredTimes = SelectedTimes.filter { it in listOf(
                getString(R.string.time_morning),
                getString(R.string.time_lunch),
                getString(R.string.time_dinner)
            ) }

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
                text = getString(R.string.five_when_taking)
                setPadding(8, 0, 8, 0)
                setTextAppearance(R.style.TagTextStyle)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2))

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams = params
            }
            binding.llSelectedTimes.addView(dosingTextView)
        }
    }

    private fun updateMealUnit(mealUnit: String) {
        binding.tvMealUnit.text = mealUnit

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(meal_unit = mealUnit)
        }
    }

    private fun updateMealTime(mealTime: Int) {
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(meal_time = mealTime)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMinutes.windowToken, 0)
    }

    private fun updateNextButtonState() {
        val isInputValid = isValidInput()
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isInputValid)
    }

    fun isValidInput(): Boolean {
        val mealUnit = binding.tvMealUnit.text.toString()
        val mealTime = binding.etMinutes.text.toString().replace(getString(R.string.five_enter_minutes), "").trim()

        return mealUnit.isNotEmpty() && mealTime.isNotEmpty() && mealTime.toIntOrNull() != null && mealTime.toInt() > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}