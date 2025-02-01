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
import coil.load
import coil.transform.RoundedCornersTransformation
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationFragment
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentPillDetailDialogBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import com.pill_mate.pill_mate_android.search.presenter.StepTwoPresenter
import com.pill_mate.pill_mate_android.search.view.PillSearchBottomSheetFragment
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
        Log.d("MedicineConflictCheck", "Checking medicine conflicts for itemSeq: $itemSeq")

        // 병용금기 데이터 가져오기
        ServiceCreator.medicineRegistrationService.getUsjntTaboo(itemSeq)
            .enqueue(object : Callback<List<UsjntTabooResponse>> {
                override fun onResponse(
                    call: Call<List<UsjntTabooResponse>>,
                    response: Response<List<UsjntTabooResponse>>
                ) {
                    val usjntTabooData = response.body().orEmpty()
                    Log.d("MedicineConflictCheck", "UsjntTabooResponse: $usjntTabooData")

                    // 병용금기 응답이 온 후 효능군 중복 API 호출
                    checkEfcyDplct(itemSeq, usjntTabooData)
                }

                override fun onFailure(call: Call<List<UsjntTabooResponse>>, t: Throwable) {
                    Log.e("MedicineConflictCheck", "Failed to fetch UsjntTabooResponse: ${t.message}")
                    navigateToStepThree()
                }
            })
    }

    private fun checkEfcyDplct(itemSeq: String, usjntTabooData: List<UsjntTabooResponse>) {
        Log.d("MedicineConflictCheck", "Checking efficiency duplicate for itemSeq: $itemSeq")

        // 효능군 중복 데이터 가져오기
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
                        navigateToStepThree()
                    }
                }

                override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                    Log.e("MedicineConflictCheck", "Failed to fetch EfcyDplctResponse: ${t.message}")
                    navigateToStepThree()
                }
            })
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

    private fun navigateToStepThree() {
        dismiss()
        bottomSheet.dismiss()
        val navController = medicineRegistrationFragment.childFragmentManager
            .findFragmentById(R.id.nav_host_fragment_steps)?.findNavController()
        navController?.navigate(R.id.action_stepTwoFragment_to_stepThreeFragment)
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