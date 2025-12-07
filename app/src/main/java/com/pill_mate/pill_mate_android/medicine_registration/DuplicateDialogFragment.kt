package com.pill_mate.pill_mate_android.medicine_registration

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.FragmentDuplicateDialogBinding

class DuplicateDialogFragment(
    private val onConfirm: () -> Unit,
    private val showMessage: Boolean = true,
    private val onCancel: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: FragmentDuplicateDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicateDialogBinding.inflate(inflater, container, false)

        // 메시지 표시 여부에 따라 visibility 조정
        binding.tvMessage.visibility = if (showMessage) View.VISIBLE else View.GONE

        binding.btnDetail.setOnClickListener {
            onConfirm()
            dismiss()
        }

        binding.btnBack.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        // 뒤로가기 방지
        isCancelable = false

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // 바깥 터치로 닫히지 않도록 설정
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}