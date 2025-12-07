package com.pill_mate.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pill_mate.pill_mate_android.ServiceCreator.medicineRegistrationService
import com.pill_mate.pill_mate_android.databinding.ActivityMedicineDetailBinding
import com.pill_mate.pill_mate_android.medicine_conflict.ConflictAdapter
import com.pill_mate.pill_mate_android.medicine_conflict.InquiryBottomSheetFragment
import com.pill_mate.pill_mate_android.medicine_conflict.PillDeleteDialogFragment
import com.pill_mate.pill_mate_android.medicine_conflict.model.ConflictRemoveResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.PharmacyAndHospital
import com.pill_mate.pill_mate_android.medicine_conflict.model.PhoneAndAddressResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse
import com.pill_mate.pill_mate_android.medicine_registration.model.Hospital
import com.pill_mate.pill_mate_android.medicine_registration.model.Pharmacy
import com.pill_mate.pill_mate_android.pillcheck.model.MedicineIdData
import com.pill_mate.pill_mate_android.pillcheck.model.ResponseMedicineDetail
import com.pill_mate.pill_mate_android.pillcheck.util.fetch
import com.pill_mate.pill_mate_android.pillsearch.ConflictMedicineAllResponse
import com.pill_mate.pill_mate_android.pillsearch.ConflictMedicineEfcyItem
import com.pill_mate.pill_mate_android.pillsearch.ConflictMedicineUsjntItem
import com.pill_mate.pill_mate_android.pillsearch.ResponseConflictMedicineDetail
import com.pill_mate.pill_mate_android.util.CustomDividerItemDecoration
import com.pill_mate.pill_mate_android.util.CustomSnackbar
import com.pill_mate.pill_mate_android.util.expandTouchArea
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicineDetailBinding
    private var medicineId: Long = -1L

    private var usjntTabooData: List<UsjntTabooResponse>? = null
    private var efcyDplctData: List<EfcyDplctResponse>? = null

    private lateinit var contraindicationAdapter: ConflictAdapter
    private lateinit var efficiencyOverlapAdapter: ConflictAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMedicineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        medicineId = intent?.getLongExtra("medicineId", -1L) ?: -1L
        val isConflictMode = intent?.getBooleanExtra("isConflictMode", false) ?: false
        Log.d("MedicineDetail", "medicineId=$medicineId, isConflictMode=$isConflictMode")


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.medicine_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        setDropdownClickListener()
        setButtonClickListener()
        setupAdapters()

        // medicineId가 유효할 경우
        if (medicineId != -1L) {
            if (isConflictMode) {
                fetchConflictMedicineDetailData(medicineId)
            } else {
                fetchMedicineDetailData(medicineId)
            }
        } else {
            Toast.makeText(this, "약물 정보가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        with(binding) {
            btnDropdown1.post {
                btnDropdown1.expandTouchArea(200) // 200dp 만큼 터치 영역 확장
            }
            btnDropdown2.post {
                btnDropdown2.expandTouchArea(200)
            }
            btnDropdown3.post {
                btnDropdown3.expandTouchArea(200)
            }
            btnDropdown4.post {
                btnDropdown4.expandTouchArea(200)
            }
            btnFaq2.post {
                btnFaq2.expandTouchArea(200)
            }
        }

        setupTooltip(binding.layoutContraindicationClickArea, binding.ivContraindicationTooltip)
        setupTooltip(binding.layoutEfficiencyOverlapClickArea, binding.ivEfficiencyOverlapTooltip)
    }

    private fun setButtonClickListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnFaq2.setOnClickListener {
            val ingredientInfoBottomSheet = IngredientInfoBottomSheetFragment()
            ingredientInfoBottomSheet.show(supportFragmentManager, ingredientInfoBottomSheet.tag)
        }

    }

    private fun setDropdownClickListener() = with(binding) {
        val dropdowns = listOf(
            Triple(btnDropdown1, layoutUserMethodInfo, line1),
            Triple(btnDropdown2, layoutEfficacyInfo, line2),
            Triple(btnDropdown3, layoutPrecautionsInfo, line3),
            Triple(btnDropdown4, layoutStorageInfo, null)
        )

        dropdowns.forEach { (button, layout, line) ->
            button.setOnClickListener {
                val isClosed = layout.visibility == View.VISIBLE
                if (isClosed) {
                    layout.visibility = View.GONE
                    button.setImageResource(R.drawable.btn_dropdown_up)
                    line?.visibility = View.VISIBLE
                } else {
                    layout.visibility = View.VISIBLE
                    button.setImageResource(R.drawable.btn_dropdown_down)
                    line?.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAdapters() { // 병용금기 리스트용 어댑터
        contraindicationAdapter = ConflictAdapter(
            onInquiryClicked = { itemSeq -> fetchPhoneAndAddress(itemSeq) },
            onDeleteClicked = { itemSeq -> showDeleteDialog(itemSeq) },
            showDeleteButton = true
        )

        // 효능군 중복 리스트용 어댑터
        efficiencyOverlapAdapter = ConflictAdapter(
            onInquiryClicked = { itemSeq -> fetchPhoneAndAddress(itemSeq) },
            onDeleteClicked = { itemSeq -> showDeleteDialog(itemSeq) },
            showDeleteButton = true
        )

        val dividerColor = ContextCompat.getColor(this, R.color.gray_3)
        val dividerHeight = 1f
        val marginStart = 12f
        val marginEnd = 12f

        binding.rvContraindication.apply {
            layoutManager = LinearLayoutManager(this@MedicineDetailActivity)
            adapter = contraindicationAdapter
            addItemDecoration(
                CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd)
            )
        }

        binding.rvEfficiencyOverlap.apply {
            layoutManager = LinearLayoutManager(this@MedicineDetailActivity)
            adapter = efficiencyOverlapAdapter
            addItemDecoration(
                CustomDividerItemDecoration(dividerHeight, dividerColor, marginStart, marginEnd)
            )
        }
    }

    // 약물 상세 데이터 받아오기(충돌O)
    private fun fetchConflictMedicineDetailData(medicineId: Long) {
        binding.showLoading()

        ServiceCreator.conflictMedicineDetailService.getConflictMedicineDetailData(medicineId)
            .enqueue(object : Callback<BaseResponse<ResponseConflictMedicineDetail>> {
                override fun onResponse(
                    call: Call<BaseResponse<ResponseConflictMedicineDetail>>,
                    response: Response<BaseResponse<ResponseConflictMedicineDetail>>
                ) {
                    binding.hideLoading()

                    val data = response.body()?.result ?: return
                    Log.d("MedicineDetail", "약물 상세 응답: $data")
                    fetchMedicineDetailUI(
                        ResponseMedicineDetail(
                            medicineImage = data.itemImage,
                            className = data.className,
                            medicineName = data.itemName,
                            entpName = data.entpName,
                            efficacy = data.efcyQesitm,
                            sideEffect = data.atpnQesitm,
                            storage = data.depositMethod,
                            userMethod = data.useMethodQesitm,
                            caution = data.typeName
                        )
                    )

                    // 병용금기 / 효능군 중복 섹션
                    updateConflictSection(data.allConflictResponse)
                }

                override fun onFailure(
                    call: Call<BaseResponse<ResponseConflictMedicineDetail>>, t: Throwable
                ) {
                    binding.hideLoading()
                    Log.e("MedicineDetail", "API 실패: ${t.message}")
                }
            })
    }

    private fun updateConflictSection(result: ConflictMedicineAllResponse) =
        with(binding) { // 충돌하지만, 관련 데이터가 없는 경우 충돌 섹션 미노출
            val noConflictData = result.usjntTabooList.isNullOrEmpty() && result.efcyDplctList.isNullOrEmpty()

            if (intent?.getBooleanExtra("isConflictMode", false) == true && noConflictData) {
                layoutConflictSection.visibility = View.GONE
                blank1.visibility = View.VISIBLE
                return@with
            }

            layoutConflictSection.visibility = View.VISIBLE
            blank1.visibility = View.GONE

            usjntTabooData = mapUsjntTabooList(result.usjntTabooList)
            efcyDplctData = mapEfcyDplctList(result.efcyDplctList)

            // 병용금기(UsjntTaboo)
            if (!result.usjntTabooList.isNullOrEmpty()) {
                layoutContraindication.visibility = View.VISIBLE
                contraindicationAdapter.submitList(usjntTabooData!!)
            } else {
                layoutContraindication.visibility = View.GONE
            }

            // 효능군중복(EfcyDplct)
            if (!result.efcyDplctList.isNullOrEmpty()) {
                layoutEfficiencyOverlap.visibility = View.VISIBLE
                efficiencyOverlapAdapter.submitList(efcyDplctData!!)
            } else {
                layoutEfficiencyOverlap.visibility = View.GONE
            }
        }

    private fun mapUsjntTabooList(list: List<ConflictMedicineUsjntItem>): List<UsjntTabooResponse> {
        return list.map {
            UsjntTabooResponse(
                mixtureItemSeq = it.mixtureItemSeq,
                className = it.className,
                mixItemName = it.mixItemName,
                entpName = it.entpName,
                prohbtContent = it.prohbtContent,
                item_image = it.image
            )
        }
    }

    private fun mapEfcyDplctList(list: List<ConflictMedicineEfcyItem>): List<EfcyDplctResponse> {
        return list.map {
            EfcyDplctResponse(
                itemSeq = it.itemSeq,
                className = it.className,
                itemName = it.itemName,
                entpName = it.entpName,
                effectName = it.effectName,
                item_image = it.image
            )
        }
    }

    // 약물 상세 데이터 받아오기(충돌X)
    private fun fetchMedicineDetailData(medicineId: Long) {
        binding.showLoading()
        ServiceCreator.medicineDetailService.postMedicineDetailData(MedicineIdData(medicineId)).fetch {
            fetchMedicineDetailUI(it)
            binding.hideLoading()
        }
    }

    private fun fetchMedicineDetailUI(data: ResponseMedicineDetail) {
        Log.d("MedicineDetail", "서버에서 받은 약물 상세 데이터: $data")

        with(binding) { // Glide로 이미지 로드
            Glide.with(root.context).load(data.medicineImage).error(R.drawable.img_default_large)
                .placeholder(R.drawable.img_default_large).into(imgMedicine)

            tvMedicineType.text = data.className
            tvMedicineName.text = data.medicineName
            tvMedicineEntp.text = data.entpName

            val cautionList = data.caution?.split(",")?.map { it.trim() } ?: emptyList()

            val pink = ContextCompat.getColorStateList(root.context, R.color.main_pink_1)
            val white = ContextCompat.getColorStateList(root.context, R.color.white)

            val cautionTags = mapOf(
                "임부금기" to tag1, "특정연령대금기" to tag2, "노인주의" to tag3, "용량주의" to tag4, "첨가제주의" to tag5, "투여기간주의" to tag6
            )

            // 데이터에 포함된 항목만 하이라이트
            for ((keyword, tagView) in cautionTags) {
                if (cautionList.contains(keyword)) {
                    tagView.backgroundTintList = pink
                    tagView.setTextColor(white)
                }
            }

            // 값이 모두 없을 경우
            val hasAnyInfo = listOf(
                data.efficacy, data.sideEffect, data.storage, data.userMethod
            ).any { !it.isNullOrBlank() }

            if (!hasAnyInfo) {
                layoutDetailInfo.visibility = View.GONE
                blank2.visibility = View.GONE
            }

            // 빈 값이 아닐 때만 표시
            if (!data.userMethod.isNullOrBlank()) {
                tvUserMethodInfo.text = data.userMethod
            } else {
                layoutTvUserMethod.visibility = View.GONE
                layoutUserMethodInfo.visibility = View.GONE
            }

            if (!data.efficacy.isNullOrBlank()) {
                tvEfficacyInfo.text = data.efficacy
            } else {
                layoutTvEfficacy.visibility = View.GONE
            }

            if (!data.sideEffect.isNullOrBlank()) {
                tvPrecautionsInfo.text = data.sideEffect
            } else {
                layoutTvPrecautions.visibility = View.GONE
            }

            if (!data.storage.isNullOrBlank()) {
                tvStorageInfo.text = data.storage
            } else {
                layoutTvStorage.visibility = View.GONE
            }
        }
    }

    private fun fetchPhoneAndAddress(itemSeq: String) {
        Log.d("MedicineConflictFragment", "Fetching phone and address for itemSeq: $itemSeq")

        medicineRegistrationService.getPhoneAndAddress(itemSeq).enqueue(object : Callback<PhoneAndAddressResponse> {
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

        binding.root.post {
            bottomSheet.show(supportFragmentManager, "inquiryBottomSheet")
        }
    }

    private fun showDeleteDialog(itemSeq: String) {
        PillDeleteDialogFragment.newInstance(itemSeq) { deletedItemSeq ->
            removeConflict(deletedItemSeq)
        }.show(supportFragmentManager, "deleteDialog")
    }

    private fun removeConflict(itemSeq: String) {
        Log.d("MedicineConflictFragment", "Removing conflict for itemSeq: $itemSeq") // 🛠 디버깅용 로그 추가

        medicineRegistrationService.removeConflict(itemSeq).enqueue(object : Callback<ConflictRemoveResponse> {
            override fun onResponse(call: Call<ConflictRemoveResponse>, response: Response<ConflictRemoveResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("MedicineConflictFragment", "Successfully removed conflict for itemSeq: $itemSeq") // 성공 로그 추가

                    disableDeleteButton(itemSeq) // 삭제 버튼 비활성화
                    showDeleteSuccessSnackbar() // 성공 메시지 표시
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("MedicineConflictFragment", "Failed to remove conflict: $errorBody") // 🛠 오류 로그 추가

                    showDeleteErrorSnackbar()
                }
            }

            override fun onFailure(call: Call<ConflictRemoveResponse>, t: Throwable) {
                Log.e(
                    "MedicineConflictFragment", "Network error while removing conflict: ${t.message}"
                ) // 🛠 네트워크 오류 로그 추가
                showDeleteErrorSnackbar()
            }
        })
    }

    private fun showDeleteSuccessSnackbar() {
        CustomSnackbar.showCustomSnackbar(
            this, binding.root, getString(R.string.medicine_conflict_delete_success)
        )
    }

    private fun showDeleteErrorSnackbar() {
        CustomSnackbar.showCustomSnackbar(
            this, binding.root, getString(R.string.medicine_conflict_delete_error)
        )
    }

    private fun disableDeleteButton(itemSeq: String) {
        val contraindicationPosition = usjntTabooData?.indexOfFirst { it.mixtureItemSeq == itemSeq }
        val efficiencyOverlapPosition = efcyDplctData?.indexOfFirst { it.itemSeq == itemSeq }

        contraindicationPosition?.let {
            val viewHolder =
                binding.rvContraindication.findViewHolderForAdapterPosition(it) as? ConflictAdapter.ViewHolder
            viewHolder?.disableDeleteButton()
        }

        efficiencyOverlapPosition?.let {
            val viewHolder =
                binding.rvEfficiencyOverlap.findViewHolderForAdapterPosition(it) as? ConflictAdapter.ViewHolder
            viewHolder?.disableDeleteButton()
        }
    }

    private fun setupTooltip(
        clickArea: View, tooltip: View
    ) {
        clickArea.setOnClickListener {
            tooltip.visibility = View.VISIBLE
        }

        // 루트 터치 시 툴팁 숨김
        binding.scrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && tooltip.visibility == View.VISIBLE) {
                tooltip.visibility = View.GONE
            }
            false
        }

        // 스크롤 시 툴팁 숨김
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (tooltip.visibility == View.VISIBLE) {
                tooltip.visibility = View.GONE
            }
        }
    }

}