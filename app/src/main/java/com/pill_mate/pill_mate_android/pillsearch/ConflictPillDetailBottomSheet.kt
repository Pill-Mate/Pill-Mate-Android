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
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConflictPillDetailBottomSheet(
    private val bottomSheet: ConflictPillSearchBottomSheetFragment,
    private val pillItem: PillIdntfcItem
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

        binding.tvPillClass.text = pillItem.CLASS_NAME
        binding.tvPillName.text = pillItem.ITEM_NAME
        binding.tvPillEntp.text = pillItem.ENTP_NAME

        if (!pillItem.ITEM_IMAGE.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(pillItem.ITEM_IMAGE)
                .transform(RoundedCorners(20))
                .into(binding.ivPillImage)
        } else {
            binding.ivPillImage.setImageResource(R.drawable.img_default)
        }

        binding.btnYes.setOnClickListener {
            if (!isProcessing) {
                isProcessing = true
                binding.btnYes.isEnabled = false
                checkMedicineConflicts(pillItem.ITEM_SEQ)
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun checkMedicineConflicts(itemSeq: String) {
        ServiceCreator.medicineRegistrationService.getUsjntTaboo(itemSeq)
            .enqueue(object : Callback<List<UsjntTabooResponse>> {
                override fun onResponse(
                    call: Call<List<UsjntTabooResponse>>,
                    response: Response<List<UsjntTabooResponse>>
                ) {
                    val usjntTabooData = response.body().orEmpty()
                    checkEfcyDplct(itemSeq, usjntTabooData)
                }

                override fun onFailure(call: Call<List<UsjntTabooResponse>>, t: Throwable) {
                    resetProcessing()
                    dismiss()
                }
            })
    }

    private fun checkEfcyDplct(itemSeq: String, usjntTabooData: List<UsjntTabooResponse>) {
        ServiceCreator.medicineRegistrationService.getEfcyDplct(itemSeq)
            .enqueue(object : Callback<List<EfcyDplctResponse>> {
                override fun onResponse(
                    call: Call<List<EfcyDplctResponse>>,
                    response: Response<List<EfcyDplctResponse>>
                ) {
                    val efcyDplctData = response.body().orEmpty()
                    val hasConflict = usjntTabooData.isNotEmpty() || efcyDplctData.isNotEmpty()
                    navigateToResultScreen(hasConflict, usjntTabooData, efcyDplctData)
                    resetProcessing()
                }

                override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                    resetProcessing()
                    dismiss()
                }
            })
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
        dismiss()              // 이 바텀시트 닫기
        bottomSheet.dismiss()  // 부모 바텀시트도 함께 닫기

        val navController = parentFragment?.findNavController()
            ?: parentFragmentManager.primaryNavigationFragment?.findNavController()
            ?: return

        val bundle = Bundle().apply {
            putParcelableArrayList("usjntTabooData", ArrayList(usjntTabooData))
            putParcelableArrayList("efcyDplctData", ArrayList(efcyDplctData))
            putString("pillName", pillItem.ITEM_NAME)
        }

        val actionId = if (hasConflict) {
            R.id.action_conflictPillSearchFragment_to_conflictCheckFragment
        } else {
            R.id.action_conflictPillSearchFragment_to_noConflictFragment
        }

        android.util.Log.d("NavigateResult", "🔍 충돌 여부: $hasConflict")

        navController.navigate(actionId, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            bottomSheet: ConflictPillSearchBottomSheetFragment,
            pillItem: PillIdntfcItem
        ): ConflictPillDetailBottomSheet {
            return ConflictPillDetailBottomSheet(bottomSheet, pillItem)
        }
    }
}