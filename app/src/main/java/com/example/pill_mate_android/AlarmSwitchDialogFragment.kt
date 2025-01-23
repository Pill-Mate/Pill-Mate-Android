package com.example.pill_mate_android

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pill_mate_android.databinding.FragmentPillRegistrationDialogBinding

class AlarmSwitchDialogFragment : DialogFragment() {

    private var _binding: FragmentPillRegistrationDialogBinding? = null
    private val binding get() = _binding!!

    interface AlarmSwitchDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    private var listener: AlarmSwitchDialogListener? = null

    fun setAlarmSwitchDialogListener(listener: AlarmSwitchDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPillRegistrationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        binding.ivWarning.setImageResource(R.drawable.ic_warning)
        binding.tvTitle.text = "복약 알림을 권장드려요!"
        binding.tvMessage1.text = "약물을 제시간에 섭취할 수 있도록 도울게요."

        binding.btnCancel.text = "아니요"
        binding.btnCancel.setOnClickListener {
            listener?.onDialogNegativeClick()
            dismiss()
        }

        binding.btnConfirm.text = "네, 설정할게요"
        binding.btnConfirm.setOnClickListener {
            listener?.onDialogPositiveClick()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}