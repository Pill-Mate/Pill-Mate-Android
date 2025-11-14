package com.pill_mate.pill_mate_android.pillsearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.GlobalApplication.Companion.amplitude
import com.pill_mate.pill_mate_android.MedicineDetailActivity
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetPillDetailBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictCheckResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictCheckResult
import com.pill_mate.pill_mate_android.medicine_registration.DuplicateDialogFragment
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConflictPillDetailBottomSheet(
    private val bottomSheet: ConflictPillSearchBottomSheetFragment, private val medicineItem: SearchMedicineItem
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetPillDetailBinding? = null
    private val binding get() = _binding!!

    private var isProcessing = false

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            Glide.with(requireContext()).load(medicineItem.itemImage).transform(RoundedCorners(20))
                .into(binding.ivPillImage)
        } else {
            binding.ivPillImage.setImageResource(R.drawable.img_default)
        }

        binding.btnYes.setOnClickListener { // 약물 상세 페이지로 이동 (충돌X)
            //충돌 검사할 약물선택 확인 버튼 클릭이벤트
            amplitude.track(
                "click_confirm_conflict_check_pill_button",
                mapOf("screen_name" to "screen_conflict_pill_detail_bottom_sheet")
            )
            if (!isProcessing) { // 버튼 클릭 딱 한번만 되게 하는 if문
                isProcessing = true
                binding.btnYes.isEnabled = false
                checkAllConflicts(medicineItem.itemSeq.toString())
            }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun checkAllConflicts(itemSeq: String) { // 약물 중복과 효능군 중복 병용 금기 한번에 확인하는 API 사용
        ServiceCreator.medicineRegistrationService.checkConflict(itemSeq)
            .enqueue(object : Callback<ConflictCheckResponse> {
                override fun onResponse(
                    call: Call<ConflictCheckResponse>, response: Response<ConflictCheckResponse>
                ) {
                    val result = response.body()?.result
                    if (result != null) {
                        handleConflictResult(result) // 통합 응답 처리 함수로 분기
                    } else {
                        moveToMedicineDetailActivity()
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

    private fun handleConflictResult(result: ConflictCheckResult) { // 약물 중복 다이얼로그랑 나머지 충돌이랑 분기
        result.usjntTabooList.forEachIndexed { index, item ->
            Log.d("UsjntTabooList", "[$index] mixtureItemSeq: ${item.mixtureItemSeq}")
            Log.d("UsjntTabooList", "[$index] className: ${item.className}")
            Log.d("UsjntTabooList", "[$index] mixItemName: ${item.mixItemName}")
            Log.d("UsjntTabooList", "[$index] entpName: ${item.entpName}")
            Log.d("UsjntTabooList", "[$index] prohbtContent: ${item.prohbtContent}")
            Log.d("UsjntTabooList", "[$index] item_image: ${item.item_image}")
        }

        // 중복 성분이 있으면 다이얼로그 표시
        if (result.conflictWithUserMeds != null) {
            val dialog = DuplicateDialogFragment(
                onConfirm = {
                    moveToConflictMedicineDetailActivity()
                    resetProcessing()
                }, showMessage = false
            )
            dialog.show(parentFragmentManager, "DuplicateDialog")
            return
        }

        moveToMedicineDetailActivity()
        resetProcessing()
    }

    private fun moveToConflictMedicineDetailActivity() { // 약물 상세 페이지로 이동(충돌O)
        val context = binding.root.context
        val intent = Intent(context, MedicineDetailActivity::class.java)
        intent.putExtra("medicineId", medicineItem.itemSeq)
        intent.putExtra("isConflictMode", true)
        context.startActivity(intent)
        dismiss()
    }

    private fun moveToMedicineDetailActivity() { // 약물 상세 페이지로 이동(충돌X)
        val context = binding.root.context
        val intent = Intent(context, MedicineDetailActivity::class.java)
        intent.putExtra("medicineId", medicineItem.itemSeq)
        intent.putExtra("isConflictMode", false)
        context.startActivity(intent)
        dismiss()
    }

    private fun resetProcessing() {
        isProcessing = false
        binding.btnYes.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            bottomSheet: ConflictPillSearchBottomSheetFragment, pillItem: SearchMedicineItem
        ): ConflictPillDetailBottomSheet {
            return ConflictPillDetailBottomSheet(bottomSheet, pillItem)
        }
    }
}