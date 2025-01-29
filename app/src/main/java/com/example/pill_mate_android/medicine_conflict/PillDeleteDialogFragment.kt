package com.example.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.example.pill_mate_android.databinding.FragmentPillDeleteDialogBinding
import com.example.pill_mate_android.medicine_conflict.model.ConflictRemoveResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // 다이얼로그 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // 다이얼로그 크기 설정
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.btnDelete.setOnClickListener {
            onDeleteSuccess(itemSeq)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
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