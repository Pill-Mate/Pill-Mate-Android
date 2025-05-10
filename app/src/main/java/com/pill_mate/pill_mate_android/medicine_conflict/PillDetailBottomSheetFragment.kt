package com.pill_mate.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetPillDetailBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
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

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetPillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isProcessing = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pillItem = arguments?.getParcelable<PillIdntfcItem>("pillItem")

        pillItem?.let {
            if (!it.ITEM_IMAGE.isNullOrEmpty()) {
                Glide.with(binding.ivPillImage.context)
                    .load(it.ITEM_IMAGE)
                    .transform(RoundedCorners(20))
                    .error(R.drawable.img_default)
                    .into(binding.ivPillImage)
            } else {
                binding.ivPillImage.setImageResource(R.drawable.img_default) // 이미지 URL 없을 경우 기본 이미지
            }

            binding.tvPillClass.text = it.CLASS_NAME
            binding.tvPillName.text = it.ITEM_NAME
            binding.tvPillEntp.text = it.ENTP_NAME
        }

        binding.btnYes.setOnClickListener {
            if (!isProcessing) {
                isProcessing = true
                binding.btnYes.isEnabled = false
                pillItem?.let { item ->
                    checkMedicineConflicts(item.ITEM_SEQ)

                    // StepTwoFragment에 선택한 약물 정보 전달
                    val result = Bundle().apply {
                        putParcelable("confirmedPillItem", item)
                    }
                    parentFragmentManager.setFragmentResult("pillConfirmResultKey", result)
                }
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
                    resetProcessingState()
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

                    if (usjntTabooData.isNotEmpty() || efcyDplctData.isNotEmpty()) {
                        navigateToLoadingConflictFragment(usjntTabooData, efcyDplctData)
                    } else {
                        dismiss()
                        // PillSearchBottomSheetFragment도 닫기
                        bottomSheet.dismiss()
                    }

                    resetProcessingState()
                }

                override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                    resetProcessingState()
                    dismiss()
                }
            })
    }

    private fun resetProcessingState() {
        isProcessing = false
        binding.btnYes.isEnabled = true
    }

    private fun navigateToLoadingConflictFragment(
        usjntTabooData: List<UsjntTabooResponse>,
        efcyDplctData: List<EfcyDplctResponse>
    ) {
        dismiss()
        bottomSheet.dismiss()

        val bundle = Bundle().apply {
            putParcelableArrayList("usjntTabooData", ArrayList(usjntTabooData))
            putParcelableArrayList("efcyDplctData", ArrayList(efcyDplctData))
        }

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
            pillItem: PillIdntfcItem
        ): PillDetailBottomSheetFragment {
            val args = Bundle()
            args.putParcelable("pillItem", pillItem)
            val fragment = PillDetailBottomSheetFragment(bottomSheet, medicineRegistrationFragment)
            fragment.arguments = args
            return fragment
        }
    }
}