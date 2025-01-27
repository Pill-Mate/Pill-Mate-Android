package com.example.pill_mate_android.pillSearch.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.pill_mate_android.LoadingConflictFragment
import com.example.pill_mate_android.MedicineRegistrationFragment
import com.example.pill_mate_android.R
import com.example.pill_mate_android.ServiceCreator
import com.example.pill_mate_android.databinding.FragmentPillDetailDialogBinding
import com.example.pill_mate_android.pillSearch.model.EfcyDplctResponse
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.UsjntTabooResponse
import com.example.pill_mate_android.pillSearch.presenter.StepTwoPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PillDetailDialogFragment(
    private val presenter: StepTwoPresenter,
    private val bottomSheet: PillSearchBottomSheetFragment, // 바텀 시트 인스턴스 전달
    private val medicineRegistrationFragment: MedicineRegistrationFragment // 추가
) : DialogFragment() {

    private var _binding: FragmentPillDetailDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val pillItem = arguments?.getParcelable<PillIdntfcItem>("pillItem")

        pillItem?.let {
            binding.ivPillImage.load(it.ITEM_IMAGE) {
                transformations(RoundedCornersTransformation(20f))
            }
            binding.tvPillClass.text = it.CLASS_NAME
            binding.tvPillName.text = it.ITEM_NAME
            binding.tvPillEntp.text = it.ENTP_NAME
        }

        binding.btnYes.setOnClickListener {
            pillItem?.let { item ->
                checkMedicineConflicts(item.ITEM_SEQ) // 병용 금기 및 약물 금기 확인
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
    }

    private fun checkMedicineConflicts(itemSeq: String) {
        var hasConflicts = false

        ServiceCreator.medicineRegistrationService.getUsjntTaboo(itemSeq)
            .enqueue(object : Callback<List<UsjntTabooResponse>> {
                override fun onResponse(
                    call: Call<List<UsjntTabooResponse>>,
                    response: Response<List<UsjntTabooResponse>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body() ?: emptyList()
                        Log.d("PillDetailDialog", "병용 금기 API 응답: $responseBody")
                        hasConflicts = responseBody.isNotEmpty()
                    } else {
                        Log.e("PillDetailDialog", "병용 금기 API 응답 실패: ${response.code()}")
                    }
                    checkEfcyDplct(itemSeq, hasConflicts)
                }

                override fun onFailure(call: Call<List<UsjntTabooResponse>>, t: Throwable) {
                    Log.e("PillDetailDialog", "병용 금기 API 호출 실패", t)
                    checkEfcyDplct(itemSeq, hasConflicts)
                }
            })
    }

    private fun checkEfcyDplct(itemSeq: String, hasConflicts: Boolean) {
        ServiceCreator.medicineRegistrationService.getEfcyDplct(itemSeq)
            .enqueue(object : Callback<List<EfcyDplctResponse>> {
                override fun onResponse(
                    call: Call<List<EfcyDplctResponse>>,
                    response: Response<List<EfcyDplctResponse>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body() ?: emptyList()
                        Log.d("PillDetailDialog", "효능 중복 데이터: $responseBody")

                        // 이 부분을 수정
                        if (responseBody.isNotEmpty()) {
                            Log.d("PillDetailDialog", "Navigating to Conflict Fragment")
                            navigateToConflictFragment()
                        } else {
                            Log.d("PillDetailDialog", "Navigating to Step Three Fragment")
                            navigateToStepThreeFragment()
                        }
                    } else {
                        Log.e("PillDetailDialog", "효능 중복 API 응답 실패: ${response.code()}")
                        navigateToStepThreeFragment() // 실패 시 기본적으로 Step Three로 이동
                    }
                }

                override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                    Log.e("PillDetailDialog", "효능 중복 API 호출 실패", t)
                    navigateToStepThreeFragment() // 실패 시 기본적으로 Step Three로 이동
                }
            })
    }

    private fun navigateToConflictFragment() {
        val pillItem = arguments?.getParcelable<PillIdntfcItem>("pillItem")
        val conflictFragment = LoadingConflictFragment().apply {
            arguments = Bundle().apply {
                putString("itemSeq", pillItem?.ITEM_SEQ)
            }
        }
        dismiss()
        bottomSheet.dismiss()
        medicineRegistrationFragment.showFullscreenFragment(conflictFragment)
    }


    private fun navigateToStepThreeFragment() {
        val parentFragment = parentFragment as? MedicineRegistrationFragment
        dismiss() // 다이얼로그 닫기
        bottomSheet.dismiss() // 바텀 시트 닫기
        parentFragment?.hideFullscreenFragment()
        parentFragment?.findNavController()?.navigate(R.id.action_stepTwoFragment_to_stepThreeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            presenter: StepTwoPresenter,
            bottomSheet: PillSearchBottomSheetFragment,
            medicineRegistrationFragment: MedicineRegistrationFragment, // 추가
            pillItem: PillIdntfcItem
        ): PillDetailDialogFragment {
            val args = Bundle()
            args.putParcelable("pillItem", pillItem)
            val fragment = PillDetailDialogFragment(presenter, bottomSheet, medicineRegistrationFragment)
            fragment.arguments = args
            return fragment
        }
    }
}