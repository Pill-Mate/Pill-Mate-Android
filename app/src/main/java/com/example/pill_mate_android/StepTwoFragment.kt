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
import com.example.pill_mate_android.databinding.FragmentStepTwoBinding
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenter
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenterImpl
import com.example.pill_mate_android.pillSearch.view.PillSearchBottomSheetFragment
import com.example.pill_mate_android.pillSearch.view.StepTwoView

class StepTwoFragment : Fragment(), StepTwoView {

    private var _binding: FragmentStepTwoBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: StepTwoPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTwoBinding.inflate(inflater, container, false)
        presenter = StepTwoPresenterImpl(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputField()

        // FragmentResultListener 설정
        setFragmentResultListener("pillSearchResultKey") { _, bundle ->
            val selectedPillName = bundle.getString("selectedPillName") ?: ""
            handleSearchResult(selectedPillName)
        }
    }

    private fun setupInputField() {
        // EditText 포커스 이벤트 처리
        binding.etPillName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                openPillSearchBottomSheet()
            }
        }

        // EditText 텍스트 변경 감지
        binding.etPillName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.handleTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun openPillSearchBottomSheet() {
        val bottomSheetFragment = PillSearchBottomSheetFragment(this)
        bottomSheetFragment.show(parentFragmentManager, "PillSearchBottomSheetFragment")
    }

    private fun handleSearchResult(selectedPillName: String) {
        binding.etPillName.setText(selectedPillName) // EditText 업데이트
        binding.etPillName.clearFocus() // 포커스 해제
        presenter.handleTextChange(selectedPillName) // Presenter 업데이트
    }

    override fun updatePillName(pillName: String) {
        binding.etPillName.setText(pillName)
        binding.etPillName.clearFocus() // 포커스 해제
        updateParentButtonState(true) // 버튼 상태 업데이트
    }

    override fun updateButtonState(isEnabled: Boolean) {
        updateParentButtonState(isEnabled)
    }

    private fun updateParentButtonState(isEnabled: Boolean) {
        (requireActivity() as? MedicineRegistrationFragment)?.updateNextButtonState(isEnabled)
    }

    fun isValidInput(): Boolean {
        return binding.etPillName.text.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}