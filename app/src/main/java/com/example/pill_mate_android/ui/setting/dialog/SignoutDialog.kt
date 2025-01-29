package com.example.pill_mate_android.ui.setting.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pill_mate_android.databinding.DialogSignoutBinding
import com.example.pill_mate_android.ui.setting.ConfirmDialogInterface

class SignoutDialog(private val confirmDialogInterface: ConfirmDialogInterface) : DialogFragment() {

    private var _binding: DialogSignoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSignoutBinding.inflate(inflater, container, false) // 레이아웃 배경을 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogButtonClickEvent()
        return binding.root
    }

    private fun dialogButtonClickEvent() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSignout.setOnClickListener {
            confirmDialogInterface.onSignOutButtonClick()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        // Dialog의 너비를 화면 너비의 80%로 설정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

