package com.pill_mate.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetPillDetailBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictCheckResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictCheckResult
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import com.pill_mate.pill_mate_android.medicine_registration.DuplicateDialogFragment
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import com.pill_mate.pill_mate_android.search.view.PillSearchBottomSheetFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PillDetailBottomSheetFragment(
    private val bottomSheet: PillSearchBottomSheetFragment,
    private val medicineRegistrationFragment: MedicineRegistrationFragment
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetPillDetailBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    private var isProcessing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetPillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pillItem = arguments?.getParcelable<SearchMedicineItem>("pillItem")

        pillItem?.let {
            if (!it.itemImage.isNullOrEmpty()) {
                Glide.with(binding.ivPillImage.context)
                    .load(it.itemImage)
                    .transform(RoundedCorners(20))
                    .error(R.drawable.img_default)
                    .into(binding.ivPillImage)
            } else {
                binding.ivPillImage.setImageResource(R.drawable.img_default)
            }

            binding.tvPillClass.text = if (it.className.isNullOrBlank()) getString(R.string.no_info) else it.className
            binding.tvPillName.text = if (it.itemName.isNullOrBlank()) getString(R.string.no_info) else it.itemName
            binding.tvPillEntp.text = if (it.entpName.isNullOrBlank()) getString(R.string.no_info) else it.entpName
        }

        binding.btnYes.setOnClickListener {
            if (!isProcessing) {
                isProcessing = true
                binding.btnYes.isEnabled = false

                pillItem?.let { item ->
                    // 통합 API로 약물 충돌 검사
                    checkAllConflicts(item)
                }
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun checkAllConflicts(pillItem: SearchMedicineItem) {
        val itemSeq = pillItem.itemSeq
        ServiceCreator.medicineRegistrationService.checkConflict(itemSeq.toString())
            .enqueue(object : Callback<ConflictCheckResponse> {
                override fun onResponse(
                    call: Call<ConflictCheckResponse>,
                    response: Response<ConflictCheckResponse>
                ) {
                    val result = response.body()?.result
                    if (result != null) {
                        handleConflictResult(result, pillItem)
                    } else {
                        resetProcessingState()
                        dismiss()
                    }
                }

                override fun onFailure(call: Call<ConflictCheckResponse>, t: Throwable) {
                    Log.e("ConflictCheck", "API 호출 실패: ${t.message}", t)
                    resetProcessingState()
                    dismiss()
                }
            })
    }

    private fun handleConflictResult(result: ConflictCheckResult, pillItem: SearchMedicineItem) {
        // 1. 이미 복용 중인 약과 중복이면 알림 다이얼로그 표시 후 함수 종료
        if (result.conflictWithUserMeds != null) {
            DuplicateDialogFragment(
                onConfirm = {
                    dismiss()
                    bottomSheet.dismiss()
                },
                showMessage = true
            ).show(parentFragmentManager, "DuplicateDialog")
            resetProcessingState()
            return
        }

        // 2. 중복이 없으면 StepTwo로 결과 전달
        sendResultToStepTwo(pillItem)

        // 3. 병용금기나 효능군 중복이 있으면 결과 화면으로 이동
        val hasTabooOrDuplication = result.usjntTabooList.isNotEmpty() || result.efcyDplctList.isNotEmpty()
        if (hasTabooOrDuplication) {
            navigateToLoadingConflictFragment(result.usjntTabooList, result.efcyDplctList, pillItem)
            resetProcessingState()
            return
        }

        // 4. 상태 초기화 및 화면 종료
        resetProcessingState()
        dismiss()
        bottomSheet.dismiss()
    }

    private fun sendResultToStepTwo(pillItem: SearchMedicineItem) {
        Log.d("TwoResult", "전달할 약물: ${pillItem.itemName}, itemSeq: ${pillItem.itemSeq}")

        val result = Bundle().apply {
            putParcelable("confirmedPillItem", pillItem)
        }
        parentFragmentManager.setFragmentResult("pillConfirmResultKey", result)
    }

    private fun resetProcessingState() {
        isProcessing = false
        binding.btnYes.isEnabled = true
    }

    // 결과 화면 이동
    private fun navigateToLoadingConflictFragment(
        usjntTabooData: List<UsjntTabooResponse>,
        efcyDplctData: List<EfcyDplctResponse>,
        pillItem: SearchMedicineItem // 추가
    ) {

        val bundle = Bundle().apply {
            putParcelableArrayList("usjntTabooData", ArrayList(usjntTabooData))
            putParcelableArrayList("efcyDplctData", ArrayList(efcyDplctData))
            putParcelable("pillItem", pillItem)
            putString("source", "medicineRegistration")
        }

        dismiss()
        bottomSheet.dismiss()

        val navController = medicineRegistrationFragment.childFragmentManager
            .findFragmentById(R.id.nav_host_fragment_steps)?.findNavController()
        navController?.navigate(R.id.action_stepTwoFragment_to_loadingConflictFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            bottomSheet: PillSearchBottomSheetFragment,
            medicineRegistrationFragment: MedicineRegistrationFragment,
            pillItem: SearchMedicineItem
        ): PillDetailBottomSheetFragment {
            val args = Bundle().apply {
                putParcelable("pillItem", pillItem)
            }
            return PillDetailBottomSheetFragment(bottomSheet, medicineRegistrationFragment).apply {
                arguments = args
            }
        }
    }
}