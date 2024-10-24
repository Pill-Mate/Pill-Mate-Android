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
import com.example.pill_mate_android.pillSearch.view.SearchPharmacyBottomSheetFragment

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
            val selectedPharmacy = bundle.getString("pharmacy") ?: ""
            binding.etPharmacy.setText(selectedPharmacy)
            binding.tvWarning.isVisible = false
            presenter.onPharmacyNameChanged(selectedPharmacy)
        }

        // EditText 클릭 시 바텀 시트 열기
        binding.etPharmacy.setOnClickListener {
            val bottomSheetFragment = SearchPharmacyBottomSheetFragment()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        // EditText 내용 변화 감지
        binding.etPharmacy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onPharmacyNameChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 다음 버튼 클릭 시 유효성 검사
        binding.btnNext.setOnClickListener {
            presenter.onNextButtonClicked(binding.etPharmacy.text.toString())
            if (binding.tvWarning.isVisible.not()) {
                findNavController().navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
            }
        }
    }

    // 버튼 상태 업데이트 (Drawable 리소스 사용)
    override fun updateButtonState(isEnabled: Boolean) {
        val backgroundResource = if (isEnabled) {
            R.drawable.bg_btn_main_blue_1
        } else {
            R.drawable.bg_btn_gray_3
        }
        binding.btnNext.setBackgroundResource(backgroundResource)

        val textColor = if (isEnabled) {
            ContextCompat.getColor(requireContext(), android.R.color.white)
        } else {
            ContextCompat.getColor(requireContext(), android.R.color.black)
        }
        binding.btnNext.setTextColor(textColor)
    }

    override fun showWarning(isVisible: Boolean) {
        binding.tvWarning.isVisible = isVisible
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        parentFragmentManager.clearFragmentResultListener("requestKey")
    }
}