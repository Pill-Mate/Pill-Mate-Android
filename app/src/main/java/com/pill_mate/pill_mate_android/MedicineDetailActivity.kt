package com.pill_mate.pill_mate_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.databinding.ActivityMedicineDetailBinding
import com.pill_mate.pill_mate_android.pillcheck.model.MedicineIdData
import com.pill_mate.pill_mate_android.pillcheck.model.ResponseMedicineDetail
import com.pill_mate.pill_mate_android.pillcheck.util.fetch
import com.pill_mate.pill_mate_android.util.expandTouchArea

class MedicineDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicineDetailBinding
    private var medicineId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMedicineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        medicineId = intent?.getLongExtra("medicineId", -1L) ?: -1L

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.medicine_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        setDropdownClickListener()
        setButtonClickListener()

        // medicineId가 유효할 경우
        if (medicineId != -1L) {
            fetchMedicineDetailData(medicineId)
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
        }
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

    // 약물 상세 데이터 받아오기
    private fun fetchMedicineDetailData(medicineId: Long) {
        binding.showLoading()
        ServiceCreator.medicineDetailService.postMedicineDetailData(MedicineIdData(medicineId)).fetch {
            updateMedicineDetailUI(it)
            binding.hideLoading()
        }
    }

    private fun updateMedicineDetailUI(data: ResponseMedicineDetail) {
        Log.d("MedicineDetail", "서버에서 받은 약물 상세 데이터: $data")

        with(binding) { // Glide로 이미지 로드
            Glide.with(root.context).load(data.medicineImage).error(R.drawable.img_default_large).transform(
                RoundedCorners(4)
            ).placeholder(R.drawable.img_default_large).into(imgMedicine)

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
}