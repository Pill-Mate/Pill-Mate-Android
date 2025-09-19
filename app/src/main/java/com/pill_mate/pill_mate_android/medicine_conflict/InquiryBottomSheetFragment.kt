package com.pill_mate.pill_mate_android.medicine_conflict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.FragmentBottomSheetInquiryBinding
import com.pill_mate.pill_mate_android.medicine_registration.model.Hospital
import com.pill_mate.pill_mate_android.medicine_registration.model.Pharmacy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pill_mate.pill_mate_android.GlobalApplication.Companion.amplitude

class InquiryBottomSheetFragment(
    private val pharmacy: Pharmacy,
    private val hospital: Hospital?
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetInquiryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetInquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        // 약국 정보 설정
        binding.tvPharmacyName.text = pharmacy.pharmacyName
        binding.tvPharmacyAddress.text = pharmacy.pharmacyAddress
        binding.layoutCallPharmacy.setOnClickListener {
            // Amplitude: '약국 전화걸기' 버튼 클릭 이벤트 로깅
            amplitude.track(
                "click_call_pharmacy_button",
                mapOf("screen_name" to "screen_inquiry_bottom_sheet")
            )

            // 기존 약국 통화 기능 호출
            makeCall(pharmacy.pharmacyPhone)
        }

        // 병원 정보 설정 (병원이 없으면 GONE 처리)
        if (hospital == null) {
            binding.layoutHospital.visibility = View.GONE
        } else {
            binding.tvHospitalName.text = hospital.hospitalName
            binding.tvHospitalAddress.text = hospital.hospitalAddress
            binding.layoutCallHospital.setOnClickListener {
                // 병원 통화 이벤트 처리
                makeCall(hospital.hospitalPhone)
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun makeCall(phoneNumber: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
            data = android.net.Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(pharmacy: Pharmacy, hospital: Hospital?): InquiryBottomSheetFragment {
            return InquiryBottomSheetFragment(pharmacy, hospital)
        }
    }
}