package com.pill_mate.pill_mate_android.schedule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.FragmentAlarmSwitchDialogBinding

class AlarmSwitchDialogFragment : DialogFragment() {

    private var _binding: FragmentAlarmSwitchDialogBinding? = null
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
        _binding = FragmentAlarmSwitchDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
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