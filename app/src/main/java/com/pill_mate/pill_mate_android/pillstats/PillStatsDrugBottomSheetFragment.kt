package com.pill_mate.pill_mate_android.pillstats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetRadiobuttonBinding
import com.pill_mate.pill_mate_android.medicine_registration.RadioButtonOptionsAdapter

class PillStatsDrugBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetRadiobuttonBinding? = null
    private val binding get() = _binding!!

    private var selectedOption: String? = null

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetRadiobuttonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = arguments?.getStringArrayList(ARG_OPTIONS).orEmpty()
        selectedOption = savedInstanceState?.getString(STATE_SELECTED) ?: arguments?.getString(ARG_SELECTED_OPTION)

        binding.tvTitle.setText(R.string.select_drug)

        binding.rvOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOptions.adapter = RadioButtonOptionsAdapter(options, selectedOption) { option ->
            selectedOption = option
            binding.btnConfirm.isEnabled = option != null
        }
        binding.rvOptions.doOnLayout { list ->
            val maxHeight = (resources.displayMetrics.heightPixels * MAX_LIST_HEIGHT_RATIO).toInt()
            if (list.height > maxHeight) {
                list.layoutParams = list.layoutParams.apply { height = maxHeight }
            }
        }

        binding.btnConfirm.isEnabled = selectedOption != null
        binding.btnConfirm.setOnClickListener {
            selectedOption?.let { option ->
                parentFragmentManager.setFragmentResult(RESULT_KEY, bundleOf(RESULT_SELECTED_DRUG to option))
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SELECTED, selectedOption)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PillStatsDrugBottomSheet"
        const val RESULT_KEY = "pill_stats_drug_result"
        const val RESULT_SELECTED_DRUG = "selected_drug"

        private const val ARG_OPTIONS = "options"
        private const val ARG_SELECTED_OPTION = "selected_option"
        private const val STATE_SELECTED = "state_selected"
        private const val MAX_LIST_HEIGHT_RATIO = 0.5f

        fun newInstance(options: List<String>, selectedOption: String?) = PillStatsDrugBottomSheetFragment().apply {
            arguments = bundleOf(
                ARG_OPTIONS to ArrayList(options),
                ARG_SELECTED_OPTION to selectedOption
            )
        }
    }
}
