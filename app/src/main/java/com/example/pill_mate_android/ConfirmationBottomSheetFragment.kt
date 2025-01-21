package com.example.pill_mate_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.pill_mate_android.databinding.FragmentBottomSheetConfirmBinding

class ConfirmationBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetConfirmBinding? = null
    private val binding get() = _binding!!
    private var onConfirmed: ((Boolean) -> Unit)? = null

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            onConfirmed?.invoke(true)
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            onConfirmed?.invoke(false)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(onConfirmed: (Boolean) -> Unit): ConfirmationBottomSheet {
            return ConfirmationBottomSheet().apply {
                this.onConfirmed = onConfirmed
            }
        }
    }
}