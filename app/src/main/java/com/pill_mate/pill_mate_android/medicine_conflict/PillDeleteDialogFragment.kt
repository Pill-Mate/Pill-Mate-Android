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

        // 뒤로가기 방지
        isCancelable = false

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
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.8).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // 바깥 터치로 닫히지 않게 설정
        dialog?.setCanceledOnTouchOutside(false)
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