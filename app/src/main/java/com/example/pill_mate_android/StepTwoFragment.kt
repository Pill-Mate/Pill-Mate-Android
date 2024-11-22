package com.example.pill_mate_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Presenter 초기화
        presenter = StepTwoPresenterImpl(this)

        // EditText 포커스 시 바텀시트 열기
        binding.etPillName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val bottomSheetFragment = PillSearchBottomSheetFragment(this)
                bottomSheetFragment.show(parentFragmentManager, "PillSearchBottomSheetFragment")
            }
        }

        // EditText 텍스트 변경에 따른 버튼 상태 업데이트
        binding.etPillName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.handleTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun updatePillName(pillName: String) {
        binding.etPillName.setText(pillName) // 선택한 약 이름 업데이트
        updateParentButtonState(true)
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