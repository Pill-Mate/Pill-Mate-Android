package com.example.pill_mate_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.pill_mate_android.databinding.FragmentStepOneBinding
import com.example.pill_mate_android.pillSearch.presenter.StepOnePresenter
import com.example.pill_mate_android.pillSearch.presenter.StepOnePresenterImpl
import com.example.pill_mate_android.pillSearch.model.SearchType
import com.example.pill_mate_android.pillSearch.view.SearchBottomSheetFragment

class StepOneFragment : Fragment(), StepOnePresenter.View {

    private var _binding: FragmentStepOneBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: StepOnePresenter.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepOneBinding.inflate(inflater, container, false)
        presenter = StepOnePresenterImpl(this) // Presenter 초기화
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FragmentResultListener 설정
        setFragmentResultListener("requestKey") { _, bundle ->
            val selectedResult = bundle.getString("selectedItem") ?: ""
            binding.etPharmacy.setText(selectedResult)
            binding.tvWarning.isVisible = false
            updateEditTextBackground(false)
            presenter.onPharmacyNameChanged(selectedResult)
        }

        // EditText 포커스 시 바텀시트 열기
        binding.etPharmacy.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                openSearchBottomSheet(SearchType.PHARMACY)
            }
        }

        // EditText 내용 변화 감지
        binding.etPharmacy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onPharmacyNameChanged(s.toString())
                updateEditTextBackground(false)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etHospital.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                openSearchBottomSheet(SearchType.HOSPITAL)
            }
        }

        // 다음 버튼 클릭 시 유효성 검사
        binding.btnNext.setOnClickListener {
            presenter.onNextButtonClicked(binding.etPharmacy.text.toString())
            if (binding.tvWarning.isVisible.not()) {
                findNavController().navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
            }
        }
    }

    private fun openSearchBottomSheet(searchType: SearchType) {
        val bottomSheetFragment = SearchBottomSheetFragment(searchType)
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    // 버튼 상태 업데이트 (Drawable 리소스 사용)
    override fun updateButtonState(isEnabled: Boolean) {
        // 버튼 배경 리소스 설정
        binding.btnNext.setBackgroundResource(
            if (isEnabled) R.drawable.bg_btn_main_blue_1 else R.drawable.bg_btn_gray_3
        )

        // 버튼 텍스트 색상 설정
        binding.btnNext.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isEnabled) android.R.color.white else android.R.color.black
            )
        )
    }

    override fun showWarning(isVisible: Boolean) {
        binding.tvWarning.isVisible = isVisible
        updateEditTextBackground(isVisible)
    }

    private fun updateEditTextBackground(isWarningVisible: Boolean) {
        binding.etPharmacy.setBackgroundResource(
            if (isWarningVisible) R.drawable.bg_edittext_red else R.drawable.bg_edittext_gray_3
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        parentFragmentManager.clearFragmentResultListener("requestKey")
    }
}