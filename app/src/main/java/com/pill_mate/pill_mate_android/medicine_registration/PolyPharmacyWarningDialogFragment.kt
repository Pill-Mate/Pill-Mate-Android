package com.pill_mate.pill_mate_android.medicine_registration

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.pill_mate.pill_mate_android.databinding.DialogPolyPharmacyWarningBinding
import com.pill_mate.pill_mate_android.main.view.MainActivity
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.util.AppPreferences

class PolyPharmacyWarningDialogFragment : DialogFragment() {

    private var _binding: DialogPolyPharmacyWarningBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPolyPharmacyWarningBinding.inflate(inflater, container, false)

        // 버튼 리스너 설정
        binding.btnCancel.setOnClickListener {
            AppPreferences.setSkipWarningDialog(requireContext(), binding.checkboxNever.isChecked)
            dismiss()
        }

        binding.btnContinue.setOnClickListener {
            AppPreferences.setSkipWarningDialog(requireContext(), binding.checkboxNever.isChecked)
            dismiss()
        }

        // 다이얼로그 바깥 터치, 뒤로가기 모두 차단
        isCancelable = false

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
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        // 바깥 영역 터치로 닫히지 않도록 설정
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun clearRegistrationData() {
        DataRepository.clearAll()

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}