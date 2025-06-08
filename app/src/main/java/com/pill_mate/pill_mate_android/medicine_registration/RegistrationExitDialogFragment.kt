package com.pill_mate.pill_mate_android.medicine_registration

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentExitRegistrationDialogBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository

class RegistrationExitDialogFragment : DialogFragment() {

    private var _binding: FragmentExitRegistrationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExitRegistrationDialogBinding.inflate(inflater, container, false)

        // 다이얼로그 닫힘 방지
        isCancelable = false

        setColoredMessage()

        binding.btnCancel.setOnClickListener {
            clearRegistrationData()
        }

        binding.btnConfirm.setOnClickListener {
            dismiss()
        }

        return binding.root
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

        // 바깥 터치로 닫히는 것 방지
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ResourceAsColor")
    private fun setColoredMessage() {
        val fullText = getString(R.string.exit_registration_message)
        val spannable = SpannableString(fullText)

        val redText = "입력하신 내용이 사라져요."
        val startIndex = fullText.indexOf(redText)

        if (startIndex >= 0) {
            val redColor = ContextCompat.getColor(requireContext(), R.color.status_red)
            spannable.setSpan(
                ForegroundColorSpan(redColor),
                startIndex, startIndex + redText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvMessage.text = spannable
    }

    private fun clearRegistrationData() {
        DataRepository.clearAll()

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        requireActivity().finish()
        dismiss()
    }
}