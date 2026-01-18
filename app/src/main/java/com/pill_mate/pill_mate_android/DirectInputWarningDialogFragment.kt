package com.pill_mate.pill_mate_android

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.FragmentDirectInputWarningDialogBinding

class DirectInputWarningDialogFragment(
    private val onConfirm: () -> Unit
) : DialogFragment() {

    private var _binding: FragmentDirectInputWarningDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectInputWarningDialogBinding.inflate(inflater, container, false)

        // 확인 버튼 클릭 리스너
        binding.btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }

        // 뒤로가기 버튼으로 닫히지 않도록 설정
        isCancelable = false

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 및 배경 설정
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.8).toInt(), // 화면 너비의 80%
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명 (둥근 모서리 적용 위함)
        }

        // 바깥 영역 터치로 닫히지 않도록 설정
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}