package com.example.pill_mate_android

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
import androidx.fragment.app.DialogFragment
import com.example.pill_mate_android.databinding.FragmentPillRegistrationDialogBinding
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.ui.main.activity.MainActivity

class PillRegistrationDialogFragment : DialogFragment() {

    private var _binding: FragmentPillRegistrationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPillRegistrationDialogBinding.inflate(inflater, container, false)

        // 부분 색상 변경된 메시지 설정
        setColoredMessage()

        binding.btnCancel.setOnClickListener {
            // 메인 액티비티로 이동
            clearRegistrationData()
        }

        binding.btnConfirm.setOnClickListener {
            dismiss()
        }

        return binding.root
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

    // 텍스트의 특정 부분만 빨간색으로 만드는 메서드
    @SuppressLint("ResourceAsColor")
    private fun setColoredMessage() {
        val fullText = "지금 종료하면 입력하신 내용이 사라져요."
        val spannable = SpannableString(fullText)

        val redText = "입력하신 내용이 사라져요."
        val startIndex = fullText.indexOf(redText)

        if (startIndex >= 0) {
            spannable.setSpan(
                ForegroundColorSpan(R.color.status_red), // 빨간색 적용
                startIndex,
                startIndex + redText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvMessage1.text = spannable
    }

    private fun clearRegistrationData() {
        DataRepository.clearAll() // 모든 데이터를 초기화

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // 기존 스택 제거 및 새로운 태스크로 시작
        startActivity(intent)
        requireActivity().finish() // 현재 액티비티 종료
        dismiss() // 다이얼로그 닫기
    }
}