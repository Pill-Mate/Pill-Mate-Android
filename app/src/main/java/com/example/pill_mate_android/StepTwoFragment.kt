package com.example.pill_mate_android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.pill_mate_android.databinding.FragmentStepTwoBinding
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenter
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenterImpl
import com.example.pill_mate_android.pillSearch.presenter.MedicineRegistrationPresenter
import com.example.pill_mate_android.pillSearch.view.PillSearchBottomSheetFragment
import com.example.pill_mate_android.pillSearch.view.StepTwoView

class StepTwoFragment : Fragment(), StepTwoView {

    private var _binding: FragmentStepTwoBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: StepTwoPresenter
    private lateinit var registrationPresenter: MedicineRegistrationPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTwoBinding.inflate(inflater, container, false)
        presenter = StepTwoPresenterImpl(this)

        // MedicineRegistrationPresenter 인스턴스를 부모 Fragment로부터 가져옴
        val parentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (parentFragment is MedicineRegistrationFragment) {
            registrationPresenter = parentFragment.getPresenter() // getter로 presenter 접근
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputField()

        // FragmentResultListener 설정 (약물 검색 결과를 받음)
        setFragmentResultListener("pillSearchResultKey") { _, bundle ->
            val selectedPillItem = bundle.getParcelable<PillIdntfcItem>("selectedPillItem")
            selectedPillItem?.let {
                handleSearchResult(it) // PillIdntfcItem 객체 전달
            }
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

    private fun handleSearchResult(selectedPillItem: PillIdntfcItem) {
        Log.d("SearchResult", "Selected pill: ${selectedPillItem.ITEM_NAME}")

        // StepTwoPresenter에 선택된 약물 객체 전달
        presenter.onPillSelected(selectedPillItem)

        // EditText에 약물 이름 업데이트
        binding.etPillName.setText(selectedPillItem.ITEM_NAME)
        binding.etPillName.clearFocus()

        // Presenter에 Schedule 데이터 업데이트 요청
        registrationPresenter.updateSchedule { schedule ->
            schedule.copy(medicine_name = selectedPillItem.ITEM_NAME)
        }
    }

    override fun updatePillName(pillName: String) {
        binding.etPillName.setText(pillName)
        binding.etPillName.clearFocus() // 포커스 해제
    }

    override fun updateButtonState(isEnabled: Boolean) {
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