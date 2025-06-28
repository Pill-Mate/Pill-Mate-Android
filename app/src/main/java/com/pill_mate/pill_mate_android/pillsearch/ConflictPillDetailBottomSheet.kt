package com.pill_mate.pill_mate_android.pillsearch

import android.os.Bundle
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
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConflictPillDetailBottomSheet(
    private val bottomSheet: ConflictPillSearchBottomSheetFragment,
    private val medicineItem: SearchMedicineItem
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetPillDetailBinding? = null
    private val binding get() = _binding!!

    private var isProcessing = false

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetPillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = getString(R.string.search_conflict_pill_detail_title)
        binding.tvPillClass.text = medicineItem.className
        binding.tvPillName.text = medicineItem.itemName
        binding.tvPillEntp.text = medicineItem.entpName

        if (!medicineItem.itemImage.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(medicineItem.itemImage)
                .transform(RoundedCorners(20))
                .into(binding.ivPillImage)
        } else {
            binding.ivPillImage.setImageResource(R.drawable.img_default)
        }

        binding.btnYes.setOnClickListener {
            if (!isProcessing) {
                isProcessing = true
                binding.btnYes.isEnabled = false
                checkAllConflicts(medicineItem.itemSeq.toString())
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun checkAllConflicts(itemSeq: String) {
        ServiceCreator.medicineRegistrationService.checkConflict(itemSeq)
            .enqueue(object : Callback<ConflictCheckResponse> {
                override fun onResponse(
                    call: Call<ConflictCheckResponse>,
                    response: Response<ConflictCheckResponse>
                ) {
                    val result = response.body()?.result
                    if (result != null) {
                        handleConflictResult(result) // 통합 응답 처리 함수로 분기
                    } else {
                        resetProcessing()
                        dismiss()
                    }
                }

                override fun onFailure(call: Call<ConflictCheckResponse>, t: Throwable) {
                    resetProcessing()
                    dismiss()
                }
            })
    }

    private fun handleConflictResult(result: ConflictCheckResult) {
        // [수정] 중복 성분이 있으면 다이얼로그 표시
        if (result.conflictWithUserMeds != null) {
            val dialog = DuplicateDialogFragment(
                onConfirm = { dismiss() },
                showMessage = false
            )
            dialog.show(parentFragmentManager, "DuplicateDialog")
            resetProcessing()
            return
        }

        // [수정] 병용금기/효능군중복 결과 화면 이동
        val hasConflict = result.usjntTabooList.isNotEmpty() || result.efcyDplctList.isNotEmpty()
        navigateToResultScreen(
            hasConflict,
            result.usjntTabooList,
            result.efcyDplctList
        )
        resetProcessing()
    }

    private fun resetProcessing() {
        isProcessing = false
        binding.btnYes.isEnabled = true
    }

    private fun navigateToResultScreen(
        hasConflict: Boolean,
        usjntTabooData: List<UsjntTabooResponse>,
        efcyDplctData: List<EfcyDplctResponse>
    ) {
        dismiss()
        bottomSheet.dismiss()

        val navController = parentFragment?.findNavController()
            ?: parentFragmentManager.primaryNavigationFragment?.findNavController()
            ?: return

        val bundle = Bundle().apply {
            putParcelableArrayList("usjntTabooData", ArrayList(usjntTabooData))
            putParcelableArrayList("efcyDplctData", ArrayList(efcyDplctData))
            putParcelable("pillItem", medicineItem)
            if (hasConflict) {
                putString("source", "pillSearch")
            }
        }

        val actionId = if (hasConflict) {
            R.id.action_conflictPillSearchFragment_to_conflictCheckFragment
        } else {
            R.id.action_conflictPillSearchFragment_to_noConflictFragment
        }

        navController.navigate(actionId, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            bottomSheet: ConflictPillSearchBottomSheetFragment,
            pillItem: SearchMedicineItem
        ): ConflictPillDetailBottomSheet {
            return ConflictPillDetailBottomSheet(bottomSheet, pillItem)
        }
    }
}