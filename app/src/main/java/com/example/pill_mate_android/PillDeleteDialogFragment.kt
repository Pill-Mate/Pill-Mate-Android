package com.example.pill_mate_android

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.pill_mate_android.databinding.FragmentPillDeleteDialogBinding
import com.example.pill_mate_android.pillSearch.api.ServerApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PillDeleteDialogFragment(
    private val medicineName: String,
    private val onDeleteSuccess: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val binding = FragmentPillDeleteDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        // 삭제 버튼 클릭 시 서버 전송
        binding.btnDelete.setOnClickListener {
            deleteMedicineFromServer(medicineName)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    private fun deleteMedicineFromServer(medicineName: String) {
        val serverApiService = ServerApiClient.serverApiService
        serverApiService.deleteMedicine(medicineName).enqueue(object : Callback<Void> {
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

    companion object {
        fun newInstance(medicineName: String, onDeleteSuccess: () -> Unit): PillDeleteDialogFragment {
            return PillDeleteDialogFragment(medicineName, onDeleteSuccess)
        }
    }
}