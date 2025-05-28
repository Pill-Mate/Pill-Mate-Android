package com.pill_mate.pill_mate_android.medicine_registration

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.DialogPolyPharmacyWarningBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository

class PolyPharmacyWarningDialogFragment(
    private val onContinue: () -> Unit
) : DialogFragment() {

    private var _binding: DialogPolyPharmacyWarningBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        _binding = DialogPolyPharmacyWarningBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        binding.btnCancel.setOnClickListener {
            clearRegistrationData()
            dismiss()
        }

        binding.btnContinue.setOnClickListener {
            dismiss()
            onContinue()
        }

        return dialog
    }

    private fun clearRegistrationData() {
        DataRepository.clearAll() // 모든 데이터를 초기화

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // 기존 스택 제거 및 새로운 태스크로 시작
        startActivity(intent)
        requireActivity().finish()
        dismiss()
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
}