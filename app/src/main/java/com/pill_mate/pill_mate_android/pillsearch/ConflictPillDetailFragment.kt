package com.pill_mate.pill_mate_android.pillsearch

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.GlobalApplication.Companion.amplitude
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.pill_mate.pill_mate_android.databinding.FragmentConflictPillDetailBinding
import com.pill_mate.pill_mate_android.medicine_conflict.ConflictAdapter
import com.pill_mate.pill_mate_android.medicine_conflict.InquiryBottomSheetFragment
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.PharmacyAndHospital
import com.pill_mate.pill_mate_android.medicine_conflict.model.PhoneAndAddressResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.Hospital
import com.pill_mate.pill_mate_android.medicine_registration.model.Pharmacy
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConflictPillDetailFragment : Fragment() {

    private var _binding: FragmentConflictPillDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var contraindicationAdapter: ConflictAdapter
    private lateinit var efficiencyOverlapAdapter: ConflictAdapter
    private var contraindicationCount = 0
    private var efficiencyOverlapCount = 0

    private var usjntTabooData: List<UsjntTabooResponse>? = null
    private var efcyDplctData: List<EfcyDplctResponse>? = null

    private var pillItem: SearchMedicineItem? = null // 클래스 안에서 선언

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConflictPillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 화면 진입(스크린뷰) 이벤트 트래킹 추가
        amplitude.track(
            "screen_view",
            mapOf("screen_name" to "screen_medicine_conflict_detail")
        )

        initializeSections()
        setupUI()
        retrieveArguments()
        loadMedicineData()
        displayConflictData()
    }

    private fun initializeSections() {
        binding.layoutContraindication.visibility = View.GONE
        binding.layoutEfficiencyOverlap.visibility = View.GONE
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        setupAdapters()

        binding.btnFinish.setOnClickListener {
            findNavController().navigate(R.id.action_medicineConflictFragment_to_ConflictPillSearchFragment)
        }

        setupTooltip(binding.layoutContraindicationClickArea, binding.ivContraindicationTooltip)
        setupTooltip(binding.layoutEfficiencyOverlapClickArea, binding.ivEfficiencyOverlapTooltip)
    }

    private fun setupAdapters() {
        contraindicationAdapter = ConflictAdapter(
            onInquiryClicked = { itemSeq -> fetchPhoneAndAddress(itemSeq) },
            onDeleteClicked = { itemSeq -> showDeleteDialog(itemSeq) },
            showDeleteButton = false)
        efficiencyOverlapAdapter = ConflictAdapter(
            onInquiryClicked = { itemSeq -> fetchPhoneAndAddress(itemSeq) },
            onDeleteClicked = { itemSeq -> showDeleteDialog(itemSeq) },
            showDeleteButton = false)

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_3) // 회색
        val dividerHeight = 1f // 1dp
        val marginStart = 12f
        val marginEnd = 12f

        binding.rvContraindication.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contraindicationAdapter
            addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))
        }

        binding.rvEfficiencyOverlap.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = efficiencyOverlapAdapter
            addItemDecoration(CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd))
        }
    }

    private fun retrieveArguments() {
        usjntTabooData = arguments?.getParcelableArrayList("usjntTabooData")
        efcyDplctData = arguments?.getParcelableArrayList("efcyDplctData")
        pillItem = arguments?.getParcelable("pillItem")

        if (usjntTabooData == null || efcyDplctData == null) {
            Log.e("ConflictPillDetailFragment", "Received null data for conflict lists")
        }

        if (pillItem == null) {
            Log.w("ConflictPillDetailFragment", "pillItem is missing from arguments")
        } else {
            Log.d("ConflictPillDetailFragment", "pillItem received: ${pillItem?.itemName}")
        }
    }

    private fun updateTitle() {
        val totalConflicts = contraindicationCount + efficiencyOverlapCount
        val titleText = getString(R.string.medicine_conflict_title, totalConflicts)
        val spannable = SpannableString(titleText)
        val startIndex = titleText.indexOf("${totalConflicts}개")
        val endIndex = startIndex + "${totalConflicts}개".length
        val pinkColor = ContextCompat.getColor(requireContext(), R.color.main_pink_1)
        spannable.setSpan(
            ForegroundColorSpan(pinkColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTitle.text = spannable
    }

    private fun loadMedicineData() {

        if (pillItem != null) {
            // 전달된 pillItem이 있는 경우
            Log.d("ConflictPillDetailFragment", "전달받은 pillItem 정보 로드: ${pillItem?.itemName}")

            binding.tvPillName.text = pillItem?.itemName
            binding.tvPillEntp.text = pillItem?.entpName
            binding.tvPillClass.text = pillItem?.className

            Glide.with(binding.ivMedicineImage.context)
                .load(pillItem?.itemImage)
                .transform(RoundedCorners(8))
                .error(R.drawable.img_default)
                .into(binding.ivMedicineImage)

        } else {
            // 없는 경우
            Log.w("ConflictPillDetailFragment", "약물 정보가 존재하지 않음")

            binding.tvPillName.text = getString(R.string.medicine_conflict_no_medicine)
            binding.tvPillEntp.text = ""
            binding.tvPillClass.text = ""
            binding.ivMedicineImage.setImageResource(R.drawable.img_default)
        }
    }

    private fun displayConflictData() {
        usjntTabooData?.let { data ->
            contraindicationCount = data.size
            contraindicationAdapter.submitList(data)

            toggleSectionVisibility(data.isNotEmpty(), SectionType.CONTRAINDICATION)

            // 툴팁 표시 (post를 사용해 렌더링 이후 처리)
            if (data.isNotEmpty()) {
                binding.ivContraindicationTooltip.post {
                    binding.ivContraindicationTooltip.visibility = View.VISIBLE
                    binding.ivContraindicationTooltip.bringToFront()
                }
            }

        } ?: run {
            contraindicationCount = 0
            toggleSectionVisibility(false, SectionType.CONTRAINDICATION)
        }

        efcyDplctData?.let { data ->
            efficiencyOverlapCount = data.size
            efficiencyOverlapAdapter.submitList(data)

            toggleSectionVisibility(data.isNotEmpty(), SectionType.EFFICIENCY_OVERLAP)

            if (data.isNotEmpty()) {
                binding.ivEfficiencyOverlapTooltip.post {
                    binding.ivEfficiencyOverlapTooltip.visibility = View.VISIBLE
                    binding.ivEfficiencyOverlapTooltip.bringToFront()
                }
            }

        } ?: run {
            efficiencyOverlapCount = 0
            toggleSectionVisibility(false, SectionType.EFFICIENCY_OVERLAP)
        }

        updateTitle()
    }

    private fun toggleSectionVisibility(isVisible: Boolean, sectionType: SectionType) {
        when (sectionType) {
            SectionType.CONTRAINDICATION -> {
                binding.layoutContraindication.visibility = if (isVisible) View.VISIBLE else View.GONE
            }

            SectionType.EFFICIENCY_OVERLAP -> {
                binding.layoutEfficiencyOverlap.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun fetchPhoneAndAddress(itemSeq: String) {
        Log.d("MedicineConflictFragment", "Fetching phone and address for itemSeq: $itemSeq")

        medicineRegistrationService.getPhoneAndAddress(itemSeq).enqueue(object :
            Callback<PhoneAndAddressResponse> {
            override fun onResponse(
                call: Call<PhoneAndAddressResponse>, response: Response<PhoneAndAddressResponse>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val result = response.body()?.result

                    // API 응답 로그 출력
                    Log.d("MedicineConflictFragment", "API Response: $result")

                    if (result != null) {
                        showInquiryBottomSheet(result)
                    } else {
                        Log.e("MedicineConflictFragment", "Empty result from API")
                    }
                } else {
                    Log.e(
                        "MedicineConflictFragment",
                        "Failed to fetch phone and address - Response Code: ${response.code()}, Error: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }

            override fun onFailure(call: Call<PhoneAndAddressResponse>, t: Throwable) {
                Log.e("MedicineConflictFragment", "API call failed: ${t.message}")
            }
        })
    }

    private fun showInquiryBottomSheet(result: PharmacyAndHospital) { // 데이터 로깅 (병원 & 약국 데이터 확인)
        Log.d("InquiryBottomSheet", "Received PharmacyAndHospital Data: $result")

        val pharmacy = Pharmacy(
            pharmacyName = result.pharmacyName,
            pharmacyAddress = result.pharmacyAddress,
            pharmacyPhone = result.pharmacyPhoneNumber
        )

        val hospital = if (result.hospitalName.isNotEmpty()) {
            Hospital(
                hospitalName = result.hospitalName,
                hospitalAddress = result.hospitalAddress,
                hospitalPhone = result.hospitalPhoneNumber
            )
        } else null

        val bottomSheet = InquiryBottomSheetFragment.newInstance(pharmacy, hospital)

        // post를 사용하여 안전하게 실행
        binding.root.post {
            bottomSheet.show(parentFragmentManager, "inquiryBottomSheet")
        }
    }

    private fun showDeleteDialog(itemSeq: String) {
        // 해당 없음
    }

    private fun setupTooltip(
        clickArea: View, tooltip: View
    ) {
        clickArea.setOnClickListener {
            tooltip.visibility = View.VISIBLE
        }

        // 루트 터치 시 툴팁 숨김
        binding.scrollContainer.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && tooltip.visibility == View.VISIBLE) {
                tooltip.visibility = View.GONE
            }
            false
        }

        // 스크롤 시 툴팁 숨김
        binding.scrollContainer.viewTreeObserver.addOnScrollChangedListener {
            if (tooltip.visibility == View.VISIBLE) {
                tooltip.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class SectionType {
        CONTRAINDICATION, EFFICIENCY_OVERLAP
    }
}