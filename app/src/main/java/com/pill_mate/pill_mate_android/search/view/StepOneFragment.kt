package com.pill_mate.pill_mate_android.search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.GlobalApplication
import com.pill_mate.pill_mate_android.GlobalApplication.Companion.amplitude
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentStepOneBinding
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.medicine_registration.PolyPharmacyWarningDialogFragment
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.PillCountCheckResponse
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.presenter.StepOnePresenter
import com.pill_mate.pill_mate_android.search.presenter.StepOnePresenterImpl
import com.pill_mate.pill_mate_android.util.AppPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StepOneFragment : Fragment(), StepOnePresenter.View {

    private var _binding: FragmentStepOneBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: StepOnePresenter.Presenter
    private var activeSearchType: SearchType? = null

    private var isDialogVisible = false  // 중복 띄우기 방지

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

        // 약물 등록 퍼널 1단계 진입
        amplitude.track(
            "funnel_registration_step_viewed",
            mapOf("step_number" to 1)
        )
        setupInputFields()
        setupSearchListeners()
        setupEndIconListeners()
    }

    override fun onResume() {
        super.onResume()
        //AppPreferences.setSkipWarningDialog(requireContext(), false) // 다이얼로그 다시 보도록 초기화
        checkPolypharmacyCount()
        updateEditTextFromDataRepository()
    }

    private fun checkPolypharmacyCount() {
        if (isDialogVisible || !AppPreferences.shouldShowWarningDialog(requireContext())) {
            return
        }

        ServiceCreator.medicineRegistrationService.checkPillCount()
            .enqueue(object : Callback<PillCountCheckResponse> {
                override fun onResponse(
                    call: Call<PillCountCheckResponse>,
                    response: Response<PillCountCheckResponse>
                ) {
                    val isSafe = response.body()?.result == true
                    if (!isSafe) {
                        showPolypharmacyWarningDialog()
                    }
                }

                override fun onFailure(call: Call<PillCountCheckResponse>, t: Throwable) {
                    android.util.Log.e("PolyCheck", "API 호출 실패: ${t.message}", t)
                }
            })
    }

    private fun showPolypharmacyWarningDialog() {
        isDialogVisible = true
        val dialog = PolyPharmacyWarningDialogFragment()
        dialog.show(parentFragmentManager, "PolyPharmacyWarningDialog")

        // 다이얼로그가 닫히면 플래그 초기화
        parentFragmentManager.executePendingTransactions()
        dialog.dialog?.setOnDismissListener {
            isDialogVisible = false
        }
    }

    private fun setupInputFields() {
        binding.etPharmacy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hospitalText = binding.etHospital.text.toString()
                presenter.onPharmacyNameChanged(s.toString(), hospitalText)
                updateClearButtonVisibility(binding.ivClearPharmacy, s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etHospital.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pharmacyText = binding.etPharmacy.text.toString()
                presenter.onPharmacyNameChanged(pharmacyText, s.toString())
                updateClearButtonVisibility(binding.ivClearHospital, s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSearchListeners() {
        binding.etPharmacy.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activeSearchType = SearchType.PHARMACY
                openSearchBottomSheet(SearchType.PHARMACY)
            }
        }

        binding.etHospital.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activeSearchType = SearchType.HOSPITAL
                openSearchBottomSheet(SearchType.HOSPITAL)
            }
        }
    }

    private fun openSearchBottomSheet(searchType: SearchType) {
        val bottomSheetFragment = SearchBottomSheetFragment(searchType) {
            updateEditTextFromDataRepository()
            clearEditTextFocus()
        }
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun updateEditTextFromDataRepository() {
        DataRepository.pharmacyData?.let {
            binding.etPharmacy.setText(it.pharmacyName)
            updateClearButtonVisibility(binding.ivClearPharmacy, binding.etPharmacy.text)
        }
        DataRepository.hospitalData?.let {
            binding.etHospital.setText(it.hospitalName)
            updateClearButtonVisibility(binding.ivClearHospital, binding.etHospital.text)
        }
    }

    private fun clearEditTextFocus() {
        binding.etPharmacy.clearFocus()
        binding.etHospital.clearFocus()
    }

    private fun setupEndIconListeners() {
        binding.ivClearPharmacy.setOnClickListener {
            binding.etPharmacy.text = null
            DataRepository.pharmacyData = null
            updateClearButtonVisibility(binding.ivClearPharmacy, null)
        }

        binding.ivClearHospital.setOnClickListener {
            binding.etHospital.text = null
            DataRepository.hospitalData = null
            updateClearButtonVisibility(binding.ivClearHospital, null)
        }
    }

    private fun updateClearButtonVisibility(imageView: View, text: CharSequence?) {
        imageView.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun onNextButtonClicked() {
        val pharmacyName = binding.etPharmacy.text.toString()
        presenter.onNextButtonClicked(pharmacyName)
    }

    override fun showWarning(isVisible: Boolean) {
        binding.tvWarning.isVisible = isVisible
        val backgroundRes = if (isVisible) R.drawable.bg_edittext_red else R.drawable.bg_edittext_black
        binding.etPharmacy.background = ContextCompat.getDrawable(requireContext(), backgroundRes)
    }

    override fun updateButtonState(isEnabled: Boolean) {
        val parent = parentFragment?.parentFragment as? MedicineRegistrationFragment
        parent?.updateNextButtonState(isEnabled)
    }

    fun isValidInput(): Boolean {
        return binding.etPharmacy.text.toString().isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}