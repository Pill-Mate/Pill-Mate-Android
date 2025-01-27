package com.example.pill_mate_android

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.example.pill_mate_android.databinding.FragmentMedicineConflictBinding
import com.example.pill_mate_android.pillSearch.model.*
import com.example.pill_mate_android.ui.main.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineConflictFragment : Fragment() {

    private var _binding: FragmentMedicineConflictBinding? = null
    private val binding get() = _binding!!

    private lateinit var contraindicationAdapter: ConflictAdapter
    private lateinit var efficiencyOverlapAdapter: ConflictAdapter
    private var contraindicationCount = 0
    private var efficiencyOverlapCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineConflictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSections()
        setupUI()
        loadMedicineData()
        fetchUsjntTabooData()
        fetchEfcyDplctData()
    }

    private fun initializeSections() {
        binding.rvContraindication.visibility = View.GONE
        binding.ivContraindicationStart.visibility = View.GONE
        binding.tvContraindication.visibility = View.GONE
        binding.ivContraindicationEnd.visibility = View.GONE

        binding.rvEfficiencyOverlap.visibility = View.GONE
        binding.ivEfficiencyOverlapStart.visibility = View.GONE
        binding.tvEfficiencyOverlap.visibility = View.GONE
        binding.ivEfficiencyOverlapEnd.visibility = View.GONE
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupAdapters()

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_medicineConflictFragment_to_stepThreeFragment)
        }

        binding.btnFinish.setOnClickListener {
            clearRegistrationData()
        }
    }

    private fun setupAdapters() {
        contraindicationAdapter = ConflictAdapter { medicineName ->
            fetchPharmacyAndHospitalData(medicineName)
        }
        binding.rvContraindication.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contraindicationAdapter
        }

        efficiencyOverlapAdapter = ConflictAdapter { medicineName ->
            fetchPharmacyAndHospitalData(medicineName)
        }
        binding.rvEfficiencyOverlap.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = efficiencyOverlapAdapter
        }
    }

    private fun updateTitle() {
        val totalConflicts = contraindicationCount + efficiencyOverlapCount
        val titleText = "복약중인 ${totalConflicts}개의 약물과\n충돌성분이 발견됐어요"

        val spannable = SpannableString(titleText)
        val startIndex = titleText.indexOf("${totalConflicts}개")
        val endIndex = startIndex + "${totalConflicts}개".length
        val pinkColor = ContextCompat.getColor(requireContext(), R.color.main_pink_1)
        spannable.setSpan(
            ForegroundColorSpan(pinkColor),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTitle.text = spannable
    }

    private fun loadMedicineData() {
        val medicine = DataRepository.getMedicine()

        if (medicine != null) {
            binding.tvPillName.text = medicine.medicine_name
            binding.tvPillEntp.text = medicine.entp_name
            binding.tvPillClass.text = medicine.classname
            binding.ivMedicineImage.load(medicine.image) {
                transformations(RoundedCornersTransformation(20f))
                crossfade(true)
                error(R.drawable.ic_default_pill)
            }
        } else {
            binding.tvPillName.text = "약물 없음"
            binding.ivMedicineImage.setImageResource(R.drawable.ic_default_pill)
        }
    }

    private fun fetchUsjntTabooData() {
        val itemSeq = arguments?.getString("itemSeq") ?: return
        medicineRegistrationService.getUsjntTaboo(itemSeq)
            .enqueue(object : Callback<List<UsjntTabooResponse>> {
                override fun onResponse(
                    call: Call<List<UsjntTabooResponse>>,
                    response: Response<List<UsjntTabooResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()
                        contraindicationCount = data.size
                        contraindicationAdapter.submitList(data)
                        toggleSectionVisibility(data.isNotEmpty(), SectionType.CONTRAINDICATION)
                    } else {
                        contraindicationCount = 0
                        toggleSectionVisibility(false, SectionType.CONTRAINDICATION)
                    }
                    updateTitle()
                }

                override fun onFailure(call: Call<List<UsjntTabooResponse>>, t: Throwable) {
                    contraindicationCount = 0
                    toggleSectionVisibility(false, SectionType.CONTRAINDICATION)
                    updateTitle()
                }
            })
    }

    private fun fetchEfcyDplctData() {
        val itemSeq = arguments?.getString("itemSeq") ?: return
        medicineRegistrationService.getEfcyDplct(itemSeq)
            .enqueue(object : Callback<List<EfcyDplctResponse>> {
                override fun onResponse(
                    call: Call<List<EfcyDplctResponse>>,
                    response: Response<List<EfcyDplctResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()
                        efficiencyOverlapCount = data.size
                        efficiencyOverlapAdapter.submitList(data)
                        toggleSectionVisibility(data.isNotEmpty(), SectionType.EFFICIENCY_OVERLAP)
                    } else {
                        efficiencyOverlapCount = 0
                        toggleSectionVisibility(false, SectionType.EFFICIENCY_OVERLAP)
                    }
                    updateTitle()
                }

                override fun onFailure(call: Call<List<EfcyDplctResponse>>, t: Throwable) {
                    efficiencyOverlapCount = 0
                    toggleSectionVisibility(false, SectionType.EFFICIENCY_OVERLAP)
                    updateTitle()
                }
            })
    }

    private fun fetchPharmacyAndHospitalData(medicineName: String) {
        medicineRegistrationService.getPharmacyAndHospital(medicineName)
            .enqueue(object : Callback<PharmacyAndHospitalResponse> {
                override fun onResponse(
                    call: Call<PharmacyAndHospitalResponse>,
                    response: Response<PharmacyAndHospitalResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            if (data.isSuccess) {
                                val result = data.result
                                val pharmacy = Pharmacy(
                                    pharmacyName = result.pharmacyName,
                                    pharmacyAddress = result.pharmacyAddress,
                                    pharmacyPhone = result.pharmacyPhone
                                )
                                val hospital = if (result.hospitalName.isNotEmpty()) {
                                    Hospital(
                                        hospitalName = result.hospitalName,
                                        hospitalAddress = result.hospitalAddress,
                                        hospitalPhone = result.hospitalPhone
                                    )
                                } else {
                                    null
                                }
                                showInquiryBottomSheet(pharmacy, hospital)
                            } else {
                                Log.e("MedicineConflict", "API Error: ${data.message}")
                            }
                        }
                    } else {
                        Log.e("MedicineConflict", "Failed to fetch data: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<PharmacyAndHospitalResponse>, t: Throwable) {
                    Log.e("MedicineConflict", "Network error: ${t.message}")
                }
            })
    }

    private fun showInquiryBottomSheet(pharmacy: Pharmacy, hospital: Hospital?) {
        val bottomSheet = InquiryBottomSheetFragment.newInstance(pharmacy, hospital)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    private fun toggleSectionVisibility(isVisible: Boolean, sectionType: SectionType) {
        when (sectionType) {
            SectionType.CONTRAINDICATION -> {
                binding.rvContraindication.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.ivContraindicationStart.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.tvContraindication.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.ivContraindicationEnd.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
            SectionType.EFFICIENCY_OVERLAP -> {
                binding.rvEfficiencyOverlap.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.ivEfficiencyOverlapStart.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.tvEfficiencyOverlap.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.ivEfficiencyOverlapEnd.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun clearRegistrationData() {
        DataRepository.clearAll()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class SectionType {
        CONTRAINDICATION,
        EFFICIENCY_OVERLAP
    }
}