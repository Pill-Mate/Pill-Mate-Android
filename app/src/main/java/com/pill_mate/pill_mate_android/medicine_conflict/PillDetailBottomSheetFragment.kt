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
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import com.pill_mate.pill_mate_android.medicine_registration.DuplicateDialogFragment
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.medicine_registration.model.DuplicateDrugResponse
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
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

            binding.tvPillClass.text = it.className
            binding.tvPillName.text = it.itemName
            binding.tvPillEntp.text = it.entpName
        }

        binding.btnYes.setOnClickListener {
            if (!isProcessing) {
                isProcessing = true
                binding.btnYes.isEnabled = false

                pillItem?.let { item ->
                    // 중복 확인 먼저 → 중복 아니면 결과 전달
                    checkDuplicateDrug(item)
                }
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun checkDuplicateDrug(pillItem: SearchMedicineItem) {
        val itemSeq = pillItem.itemSeq

        ServiceCreator.medicineRegistrationService.checkDuplicateDrug(itemSeq.toString())
            .enqueue(object : Callback<DuplicateDrugResponse> {
                override fun onResponse(
                    call: Call<DuplicateDrugResponse>,
                    response: Response<DuplicateDrugResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            // 중복이면 입력 없이 닫기
                            val dialog = DuplicateDialogFragment(
                                onConfirm = {
                                    dismiss()
                                    bottomSheet.dismiss()
                                },
                                showMessage = true // 메세지 표시
                            )
                            dialog.show(parentFragmentManager, "DuplicateDialog")
                        } else {
                            // 중복 아님 → 바로 입력 (EditText 업데이트용)
                            sendResultToStepTwo(pillItem)
                            checkMedicineConflicts(itemSeq.toString())
                        }
                    } else {
                        // 응답 실패 → 그래도 입력은 수행 (fail-safe)
                        sendResultToStepTwo(pillItem)
                        checkMedicineConflicts(itemSeq.toString())
                    }
                }

                override fun onFailure(call: Call<DuplicateDrugResponse>, t: Throwable) {
                    Log.e("DuplicateCheck", "API 호출 실패: ${t.message}", t)
                    // 네트워크 오류 등 → 그래도 입력은 수행
                    sendResultToStepTwo(pillItem)
                    checkMedicineConflicts(itemSeq.toString())
                }
            })
    }

    private fun sendResultToStepTwo(pillItem: SearchMedicineItem) {
        val result = Bundle().apply {
            putParcelable("confirmedPillItem", pillItem)
        }
        parentFragmentManager.setFragmentResult("pillConfirmResultKey", result)
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
            putString("source", "medicineRegistration")
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