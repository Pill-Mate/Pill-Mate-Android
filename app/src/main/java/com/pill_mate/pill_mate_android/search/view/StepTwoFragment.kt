package com.pill_mate.pill_mate_android.search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentStepTwoBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.search.presenter.StepTwoPresenter
import com.pill_mate.pill_mate_android.search.presenter.StepTwoPresenterImpl
import com.pill_mate.pill_mate_android.medicine_registration.presenter.MedicineRegistrationPresenter
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem

class StepTwoFragment : Fragment(), StepTwoView {

    private var _binding: FragmentStepTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: StepTwoPresenter
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    private var isDirectInputMode = false
    private var lastSavedText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTwoBinding.inflate(inflater, container, false)
        presenter = StepTwoPresenterImpl(this)

        val parentFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter()
        }

        // PillSearchBottomSheetFragment → 직접입력 요청 수신
        setFragmentResultListener("requestDirectInput") { _, bundle ->
            val query = bundle.getString("directInputQuery", "")
            switchToDirectInputMode(query)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputField()

        // 약 상세에서 YES 눌렀을 때만 반영
        setFragmentResultListener("pillConfirmResultKey") { _, bundle ->
            val confirmedPillItem =
                bundle.getParcelable<SearchMedicineItem>("confirmedPillItem")
            confirmedPillItem?.let {
                handleSearchResult(it)
            }
        }
    }

    private fun setupInputField() {
        binding.etPillName.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !isDirectInputMode) {
                    openPillSearchBottomSheet()
                }
            }

        binding.etPillName.setOnClickListener {
            if (!isDirectInputMode) {
                openPillSearchBottomSheet()
            }
        }

        binding.etPillName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                presenter.handleTextChange(s.toString())
                updateClearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                if (!isDirectInputMode) return

                val text = s?.toString().orEmpty()
                if (text == lastSavedText) return

                lastSavedText = text

                registrationPresenter.updateSchedule { schedule ->
                    schedule.copy(
                        medicine_name = text,
                        medicine_id = 0
                    )
                }
            }
        })

        binding.ivClear.setOnClickListener {
            binding.etPillName.text?.clear()
            updateClearButtonVisibility(null)

            isDirectInputMode = false
            lastSavedText = null

            registrationPresenter.updateSchedule { schedule ->
                schedule.copy(
                    medicine_name = "",
                    medicine_id = 0
                )
            }
            DataRepository.clearMedicine()
        }

        updateClearButtonVisibility(binding.etPillName.text)
    }

    private fun openPillSearchBottomSheet() {
        val medicineRegistrationFragment =
            (parentFragment?.parentFragment as? MedicineRegistrationFragment)
                ?: (requireActivity().supportFragmentManager
                    .findFragmentById(R.id.fragment_container) as? MedicineRegistrationFragment)

        if (medicineRegistrationFragment != null) {
            val bottomSheetFragment =
                PillSearchBottomSheetFragment.newInstance(this, medicineRegistrationFragment)

            bottomSheetFragment.setOnDismissListener {
                Log.d("StepTwoFragment", "PillSearchBottomSheet dismissed")
                binding.etPillName.clearFocus()
            }

            bottomSheetFragment.show(
                parentFragmentManager,
                "PillSearchBottomSheetFragment"
            )
        } else {
            Log.e("StepTwoFragment", "Could not find MedicineRegistrationFragment")
        }
    }

    private fun handleSearchResult(selectedPillItem: SearchMedicineItem) {
        presenter.onPillSelected(selectedPillItem)

        isDirectInputMode = false
        lastSavedText = null

        binding.etPillName.setText(selectedPillItem.itemName)
        binding.etPillName.clearFocus()

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                medicine_name = selectedPillItem.itemName,
                medicine_id = selectedPillItem.itemSeq
                    .takeIf { it <= Int.MAX_VALUE }
                    ?.toInt() ?: 0
            )
        }
    }

    // 직접 입력 모드 진입
    private fun switchToDirectInputMode(query: String) {
        isDirectInputMode = true
        lastSavedText = query

        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(
                medicine_name = query,
                medicine_id = 0
            )
        }

        binding.etPillName.setText(query)
        binding.etPillName.setSelection(query.length)
        binding.etPillName.requestFocus()

        presenter.handleTextChange(query)
    }

    override fun updatePillName(pillName: String) {
        binding.etPillName.setText(pillName)
        binding.etPillName.clearFocus()
        updateClearButtonVisibility(pillName)
    }

    private fun updateClearButtonVisibility(text: CharSequence?) {
        binding.ivClear.visibility =
            if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun updateButtonState(isEnabled: Boolean) {
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isEnabled)
    }

    fun isValidInput(): Boolean {
        return binding.etPillName.text.toString().isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}