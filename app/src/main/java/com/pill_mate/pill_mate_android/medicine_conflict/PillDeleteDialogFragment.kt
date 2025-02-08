package com.pill_mate.pill_mate_android.medicine_conflict

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.FragmentPillDeleteDialogBinding

class PillDeleteDialogFragment(
    private val itemSeq: String,
    private val onDeleteSuccess: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentPillDeleteDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDelete.setOnClickListener {
            onDeleteSuccess(itemSeq)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(), // 화면 너비의 80%
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(itemSeq: String, onDeleteSuccess: (String) -> Unit): PillDeleteDialogFragment {
            return PillDeleteDialogFragment(itemSeq, onDeleteSuccess)
        }
    }
}