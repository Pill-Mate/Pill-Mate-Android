package com.pill_mate.pill_mate_android.ui.setting.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.DialogLogoutBinding
import com.pill_mate.pill_mate_android.ui.setting.ConfirmDialogInterface

class LogoutDialog(private val confirmDialogInterface: ConfirmDialogInterface) : DialogFragment() {

    private var _binding: DialogLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogLogoutBinding.inflate(inflater, container, false) // 레이아웃 배경을 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogButtonClickEvent()
        return binding.root
    }

    private fun dialogButtonClickEvent() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnLogout.setOnClickListener {
            confirmDialogInterface.onLogOutButtonClick()
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