package com.example.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.example.pill_mate_android.databinding.FragmentPillDeleteDialogBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PillDeleteDialogFragment(
    private val medicineName: String,
    private val onDeleteSuccess: () -> Unit
) : DialogFragment() {

    private var _binding: FragmentPillDeleteDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 삭제 버튼 클릭 시 서버 요청
        binding.btnDelete.setOnClickListener {
            deleteMedicineFromServer(medicineName)
        }

        // 취소 버튼 클릭 시 다이얼로그 닫기
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun deleteMedicineFromServer(medicineName: String) {
        medicineRegistrationService.deleteMedicine(medicineName)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        onDeleteSuccess()
                        dismiss()
                    } else {
                        Log.e("PillDeleteDialog", "Failed to delete medicine: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("PillDeleteDialog", "Network error: ${t.message}")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(medicineName: String, onDeleteSuccess: () -> Unit): PillDeleteDialogFragment {
            return PillDeleteDialogFragment(medicineName, onDeleteSuccess)
        }
    }
}