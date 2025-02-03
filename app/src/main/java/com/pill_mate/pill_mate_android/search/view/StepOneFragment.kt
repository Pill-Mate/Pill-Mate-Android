package com.pill_mate.pill_mate_android.search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.databinding.FragmentStepOneBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.presenter.StepOnePresenter
import com.pill_mate.pill_mate_android.search.presenter.StepOnePresenterImpl

class StepOneFragment : Fragment(), StepOnePresenter.View {

    private var _binding: FragmentStepOneBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: StepOnePresenter.Presenter

    private var activeSearchType: SearchType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepOneBinding.inflate(inflater, container, false)
        presenter = StepOnePresenterImpl(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputFields()
        setupSearchListeners()
    }

    private fun setupInputFields() {
        // 약국 입력 감지
        binding.etPharmacy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onPharmacyNameChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSearchListeners() {
        // 약국 검색
        binding.etPharmacy.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activeSearchType = SearchType.PHARMACY
                openSearchBottomSheet(SearchType.PHARMACY)
            }
        }

        // 병원 검색
        binding.etHospital.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activeSearchType = SearchType.HOSPITAL
                openSearchBottomSheet(SearchType.HOSPITAL)
            }
        }
    }

    private fun openSearchBottomSheet(searchType: SearchType) {
        val bottomSheetFragment = SearchBottomSheetFragment(searchType) {
            // BottomSheet가 닫힐 때 호출되는 콜백
            updateEditTextFromDataRepository()
            clearEditTextFocus()
        }
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun updateEditTextFromDataRepository() {
        // DataRepository에서 데이터 가져와서 EditText에 반영
        DataRepository.pharmacyData?.let {
            binding.etPharmacy.setText(it.pharmacyName)
            binding.etPharmacy.clearFocus()
        }

        DataRepository.hospitalData?.let {
            binding.etHospital.setText(it.hospitalName)
            binding.etHospital.clearFocus()
        }
    }

    private fun clearEditTextFocus() {
        binding.etPharmacy.clearFocus()
        binding.etHospital.clearFocus()
    }

    override fun onResume() {
        super.onResume()

        // DataRepository에서 데이터 가져와서 EditText에 반영
        DataRepository.pharmacyData?.let {
            binding.etPharmacy.setText(it.pharmacyName)
        }

        DataRepository.hospitalData?.let {
            binding.etHospital.setText(it.hospitalName)
        }
    }

    override fun updateButtonState(isEnabled: Boolean) {
        (parentFragment as? MedicineRegistrationFragment)?.updateNextButtonState(isEnabled)
    }

    fun onNextButtonClicked() {
        val pharmacyName = binding.etPharmacy.text.toString()
        presenter.onNextButtonClicked(pharmacyName)
    }

    override fun showWarning(isVisible: Boolean) {
        binding.tvWarning.isVisible = isVisible
    }

    fun isValidInput(): Boolean {
        return binding.etPharmacy.text.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}