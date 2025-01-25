package com.example.pill_mate_android.pillSearch.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.FragmentBottomSheetCheckBinding
import com.example.pill_mate_android.pillSearch.model.BottomSheetType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CheckBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetCheckBinding
    private var selectedOption: String? = null // 선택된 옵션 저장

    companion object {
        private const val ARG_TYPE = "type"
        private const val ARG_SELECTED_OPTION = "selected_option"

        fun newInstance(type: BottomSheetType, selectedOption: String?): CheckBottomSheetFragment {
            val fragment = CheckBottomSheetFragment()
            val args = Bundle().apply {
                putSerializable(ARG_TYPE, type)
                putString(ARG_SELECTED_OPTION, selectedOption)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetCheckBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val type = arguments?.getSerializable(ARG_TYPE) as? BottomSheetType
        selectedOption = arguments?.getString(ARG_SELECTED_OPTION)

        val options = when (type) {
            BottomSheetType.MEAL_TIME_0 -> {
                binding.tvTitle.text = "복용 시간대를 선택하세요"
                listOf("식전", "식후")
            }BottomSheetType.MEAL_TIME_1 -> {
                binding.tvTitle.text = "복용 시간대를 선택하세요"
                listOf("식전", "식후", "식간")
            }
            BottomSheetType.DOSAGE_UNIT -> {
                binding.tvTitle.text = "투약 단위를 선택해주세요"
                listOf("정(개)", "캡슐", "ml", "포", "주사")
            }
            BottomSheetType.VOLUME_UNIT -> {
                binding.tvTitle.text = "투약 단위를 선택하세요"
                listOf("mg", "mcg", "g", "ml")
            }
            else -> emptyList()
        }

        val adapter = CheckOptionsAdapter(options, selectedOption) { selectedOption ->
            this.selectedOption = selectedOption
            // 선택된 항목이 있으면 버튼 활성화, 없으면 비활성화
            binding.btnConfirm.isEnabled = selectedOption != null
        }

        binding.rvOptions.layoutManager = LinearLayoutManager(context)
        binding.rvOptions.adapter = adapter

        // 초기 상태에 따라 버튼 활성화
        binding.btnConfirm.isEnabled = selectedOption != null

        // "확인" 버튼 클릭 리스너
        binding.btnConfirm.setOnClickListener {
            selectedOption?.let {
                parentFragmentManager.setFragmentResult(
                    "selectedOptionKey",
                    Bundle().apply { putString("selectedOption", it) }
                )
            }
            dismiss()
        }
    }
}