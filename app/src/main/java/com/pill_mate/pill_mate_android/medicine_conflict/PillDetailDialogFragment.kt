package com.pill_mate.pill_mate_android.medicine_conflict

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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

class PillDetailDialogFragment(
    private val bottomSheet: PillSearchBottomSheetFragment, // 바텀 시트 인스턴스 전달
    private val medicineRegistrationFragment: MedicineRegistrationFragment // 추가
) : DialogFragment() {

    private var _binding: FragmentBottomSheetPillDetailBinding? = null
    private val binding get() = _binding!!

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

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val pillItem = arguments?.getParcelable<PillIdntfcItem>("pillItem")

        pillItem?.let {
            Glide.with(binding.ivPillImage.context)
                .load(it.ITEM_IMAGE)
                .transform(RoundedCorners(20))
                .into(binding.ivPillImage)

            binding.tvPillClass.text = it.CLASS_NAME
            binding.tvPillName.text = it.ITEM_NAME
            binding.tvPillEntp.text = it.ENTP_NAME
        }

        binding.btnYes.setOnClickListener {
            if (!isProcessing) {
                isProcessing = true
                binding.btnYes.isEnabled = false // 버튼 비활성화
                pillItem?.let { item ->
                    checkMedicineConflicts(item.ITEM_SEQ)
                }
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }
        private fun checkMedicineConflicts(itemSeq: String) {
            Log.d("MedicineConflictCheck", "Checking medicine conflicts for itemSeq: $itemSeq")

            ServiceCreator.medicineRegistrationService.getUsjntTaboo(itemSeq)
                .enqueue(object : Callback<List<UsjntTabooResponse>> {
                    override fun onResponse(
                        call: Call<List<UsjntTabooResponse>>,
                        response: Response<List<UsjntTabooResponse>>
                    ) {
                        val usjntTabooData = response.body().orEmpty()
                        Log.d("MedicineConflictCheck", "UsjntTabooResponse: $usjntTabooData")
                        checkEfcyDplct(itemSeq, usjntTabooData)
                    }

                    override fun onFailure(call: Call<List<UsjntTabooResponse>>, t: Throwable) {
                        Log.e("MedicineConflictCheck", "Failed to fetch UsjntTabooResponse: ${t.message}")
                        resetProcessingState() // 상태 초기화
                        dismiss() // 다이얼로그 닫기
                    }
                })
        }

        private fun checkEfcyDplct(itemSeq: String, usjntTabooData: List<UsjntTabooResponse>) {
            Log.d("MedicineConflictCheck", "Checking efficiency duplicate for itemSeq: $itemSeq")

            ServiceCreator.medicineRegistrationService.getEfcyDplct(itemSeq)
                .enqueue(object : Callback<List<EfcyDplctResponse>> {
                    override fun onResponse(
                        call: Call<List<EfcyDplctResponse>>,
                        response: Response<List<EfcyDplctResponse>>
                    ) {
                        val efcyDplctData = response.body().orEmpty()
                        Log.d("MedicineConflictCheck", "EfcyDplctResponse: $efcyDplctData")

                        if (usjntTabooData.isNotEmpty() || efcyDplctData.isNotEmpty()) {
                            navigateToLoadingConflictFragment(usjntTabooData, efcyDplctData)
                        } else {
                            dismiss() // 다이얼로그 닫기
                        }

                        resetProcessingState() // 모든 응답이 완료되면 상태 초기화
                    }

                    override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                        Log.e("MedicineConflictCheck", "Failed to fetch EfcyDplctResponse: ${t.message}")
                        resetProcessingState()
                        dismiss() // 다이얼로그 닫기
                    }
                })
        }

        //네트워크 요청이 끝났을 때 isProcessing 상태를 초기화하고 버튼을 다시 활성화
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
            medicineRegistrationFragment: MedicineRegistrationFragment, // 추가
            pillItem: PillIdntfcItem
        ): PillDetailDialogFragment {
            val args = Bundle()
            args.putParcelable("pillItem", pillItem)
            val fragment = PillDetailDialogFragment(bottomSheet, medicineRegistrationFragment)
            fragment.arguments = args
            return fragment
        }
    }
}