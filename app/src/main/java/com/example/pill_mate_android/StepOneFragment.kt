package com.example.pill_mate_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.pill_mate_android.databinding.FragmentStepOneBinding
import com.example.pill_mate_android.pillSearch.presenter.StepOnePresenter
import com.example.pill_mate_android.pillSearch.presenter.StepOnePresenterImpl
import com.example.pill_mate_android.pillSearch.model.SearchType
import com.example.pill_mate_android.pillSearch.view.SearchBottomSheetFragment

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

        // FragmentResultListener 설정
        setFragmentResultListener("requestKey") { _, bundle ->
            val selectedResult = bundle.getString("selectedItem") ?: ""
            handleSearchResult(selectedResult)
        }
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
        val bottomSheetFragment = SearchBottomSheetFragment(searchType)
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun handleSearchResult(selectedResult: String) {
        when (activeSearchType) {
            SearchType.PHARMACY -> {
                binding.etPharmacy.setText(selectedResult)
                binding.etPharmacy.clearFocus()
                presenter.onPharmacyNameChanged(selectedResult)
            }
            SearchType.HOSPITAL -> {
                binding.etHospital.setText(selectedResult)
                binding.etHospital.clearFocus()
            }
            else -> return
        }
        binding.tvWarning.isVisible = false
        activeSearchType = null
    }

    override fun updateButtonState(isEnabled: Boolean) {
        (requireActivity() as? MedicineRegistrationFragment)?.updateNextButtonState(isEnabled)
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
        parentFragmentManager.clearFragmentResultListener("requestKey")
    }
}