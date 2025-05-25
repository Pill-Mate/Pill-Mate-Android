package com.pill_mate.pill_mate_android.pillsearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pill_mate.pill_mate_android.databinding.FragmentConflictPillSearchBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity

class ConflictPillSearchFragment : Fragment(), PillSearchResultView {

    private var _binding: FragmentConflictPillSearchBinding? = null
    private val binding get() = _binding!!

    private var selectedPillName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConflictPillSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInputField()

        binding.etPillName.setOnClickListener {
            showPillSearchBottomSheet()
        }
    }

    private fun showPillSearchBottomSheet() {
        val bottomSheet = ConflictPillSearchBottomSheetFragment.newInstance(this)

        bottomSheet.setOnDismissListener {
            selectedPillName?.let { name ->
                binding.etPillName.setText(name)
                binding.etPillName.clearFocus()
                selectedPillName = null
            }
        }

        bottomSheet.show(parentFragmentManager, "PillSearchBottomSheet")
    }

    override fun onPillSelected(pillName: String) {
        selectedPillName = pillName
    }

    private fun setupInputField() {
        // 텍스트 변경 감지
        binding.etPillName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 클리어 버튼 클릭 시 입력값 초기화
        binding.ivClear.setOnClickListener {
            binding.etPillName.text?.clear()
            updateClearButtonVisibility(null)
        }

        // 초기 상태 설정
        updateClearButtonVisibility(binding.etPillName.text)
    }

    private fun updateClearButtonVisibility(text: CharSequence?) {
        binding.ivClear.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setDefaultStatusBar()
        (activity as? MainActivity)?.showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}