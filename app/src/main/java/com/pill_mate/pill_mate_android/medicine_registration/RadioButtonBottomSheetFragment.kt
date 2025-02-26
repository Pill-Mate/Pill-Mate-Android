package com.pill_mate.pill_mate_android.medicine_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetRadiobuttonBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RadioButtonBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetRadiobuttonBinding
    private var selectedOption: String? = null // 선택된 옵션 저장

    companion object {
        private const val ARG_TYPE = "type"
        private const val ARG_SELECTED_OPTION = "selected_option"

        fun newInstance(type: BottomSheetType, selectedOption: String?): RadioButtonBottomSheetFragment {
            return RadioButtonBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TYPE, type)
                    putString(ARG_SELECTED_OPTION, selectedOption)
                }
            }
        }
    }

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetRadiobuttonBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val type = arguments?.getSerializable(ARG_TYPE) as? BottomSheetType
        selectedOption = arguments?.getString(ARG_SELECTED_OPTION)

        val options = when (type) {
            BottomSheetType.MEAL_TIME -> {
                binding.tvTitle.setText(R.string.select_meal_time)
                listOf(getString(R.string.meal_time_before_meal), getString(R.string.meal_time_after_meal))
            }
            BottomSheetType.DOSAGE_UNIT -> {
                binding.tvTitle.setText(R.string.select_dosage_unit)
                listOf(
                    getString(R.string.dosage_unit_tablet),
                    getString(R.string.dosage_unit_capsule),
                    getString(R.string.dosage_unit_ml),
                    getString(R.string.dosage_unit_packet),
                    getString(R.string.dosage_unit_injection)
                )
            }
            BottomSheetType.VOLUME_UNIT -> {
                binding.tvTitle.setText(R.string.select_volume_unit)
                listOf(
                    getString(R.string.volume_unit_mg),
                    getString(R.string.volume_unit_mcg),
                    getString(R.string.volume_unit_g),
                    getString(R.string.volume_unit_ml)
                )
            }
            else -> emptyList()
        }

        setupRecyclerView(options)
        setupConfirmButton()
    }

    private fun setupRecyclerView(options: List<String>) {
        val adapter = RadioButtonOptionsAdapter(options, selectedOption) { newSelectedOption ->
            selectedOption = newSelectedOption
            binding.btnConfirm.isEnabled = newSelectedOption != null
        }

        binding.rvOptions.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirm.apply {
            isEnabled = selectedOption != null // 초기 비활성화 처리
            setOnClickListener {
                selectedOption?.let {
                    parentFragmentManager.setFragmentResult(
                        "selectedOptionKey",
                        Bundle().apply { putString("selectedOption", it) }
                    )
                    dismiss()
                }
            }
        }
    }
}