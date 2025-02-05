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
        setupEndIconListeners()
    }

    private fun setupInputFields() {
        // 약국 입력 감지
        binding.etPharmacy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onPharmacyNameChanged(s.toString())
                updateClearButtonVisibility(binding.ivClearPharmacy, s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 병원 입력 감지
        binding.etHospital.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(binding.ivClearHospital, s)
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
            updateClearButtonVisibility(binding.ivClearPharmacy, binding.etPharmacy.text)
        }

        DataRepository.hospitalData?.let {
            binding.etHospital.setText(it.hospitalName)
            binding.etHospital.clearFocus()
            updateClearButtonVisibility(binding.ivClearHospital, binding.etHospital.text)
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
            updateClearButtonVisibility(binding.ivClearPharmacy, binding.etPharmacy.text)
        }

        DataRepository.hospitalData?.let {
            binding.etHospital.setText(it.hospitalName)
            updateClearButtonVisibility(binding.ivClearHospital, binding.etHospital.text)
        }
    }

    private fun setupEndIconListeners() {
        // 약국 X 버튼 클릭 시
        binding.ivClearPharmacy.setOnClickListener {
            clearPharmacyData()
        }

        // 병원 X 버튼 클릭 시
        binding.ivClearHospital.setOnClickListener {
            clearHospitalData()
        }
    }

    private fun clearPharmacyData() {
        binding.etPharmacy.text = null // EditText 내용 지우기
        DataRepository.pharmacyData = null // DataRepository에서 약국 데이터 삭제
        updateClearButtonVisibility(binding.ivClearPharmacy, null)
    }

    private fun clearHospitalData() {
        binding.etHospital.text = null // EditText 내용 지우기
        DataRepository.hospitalData = null // DataRepository에서 병원 데이터 삭제
        updateClearButtonVisibility(binding.ivClearHospital, null)
    }

    private fun updateClearButtonVisibility(imageView: View, text: CharSequence?) {
        imageView.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
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
        return binding.etPharmacy.text.toString().isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}