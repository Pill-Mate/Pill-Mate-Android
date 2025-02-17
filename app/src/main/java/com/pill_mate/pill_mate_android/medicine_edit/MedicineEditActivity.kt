package com.pill_mate.pill_mate_android.medicine_edit

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator.medicineEditService
import com.pill_mate.pill_mate_android.ServiceCreator.mockMedicineEditService
import com.pill_mate.pill_mate_android.databinding.ActivityMedicineEditBinding
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditInfo
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditResponse
import com.pill_mate.pill_mate_android.medicine_registration.*
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
import com.pill_mate.pill_mate_android.util.CustomChip
import com.pill_mate.pill_mate_android.util.DateConversionUtil
import com.pill_mate.pill_mate_android.util.TranslationUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicineEditBinding
    private var itemSeq: String? = null
    private var medicineImage: String? = null

    // 변수 저장
    private var intakeFrequency: List<String> = emptyList()
    private var intakeCount: List<String> = emptyList()
    private var mealUnit: String? = null
    private var mealTime: Int? = null
    private var eatCount: Int? = null
    private var eatUnit: String? = null
    private var startDate: String? = null
    private var intakePeriod: Int? = null
    private var medicineVolume: Float? = null
    private var medicineUnit: String? = null
    private var isAlarmOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemSeq = intent.getStringExtra("ITEM_SEQ")

        fetchInitialData()
        setupClickListeners()
        setupSaveButton()
    }

    private fun fetchInitialData() {
        if (itemSeq == null) {
            Toast.makeText(this, "약물 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        //medicineEditService
        mockMedicineEditService.getMedicineInfo(itemSeq!!).enqueue(object :
            Callback<MedicineEditResponse> {
            override fun onResponse(call: Call<MedicineEditResponse>, response: Response<MedicineEditResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        updateUI(data)
                    }
                } else {
                    Toast.makeText(this@MedicineEditActivity, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MedicineEditResponse>, t: Throwable) {
                Toast.makeText(this@MedicineEditActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(data: MedicineEditResponse) {
        val info = data.result // result 내부 데이터 사용

        binding.apply {
            // 기본 정보
            tvPillName.text = info.medicineName
            tvClassName.text = info.className
            tvCompanyName.text = info.entpName

            // 복약 정보 매핑
            intakeFrequency = info.intakeFrequencys.mapNotNull { TranslationUtil.translateDayToKorean(it) }
            intakeCount = info.intakeCounts.mapNotNull { TranslationUtil.translateTimeToKorean(it) }
            mealUnit = TranslationUtil.translateMealUnitToKorean(info.mealUnit ?: "")
            mealTime = info.mealTime
            eatCount = info.eatCount
            eatUnit = TranslationUtil.translateEatUnitToKorean(info.eatUnit)
            startDate = info.startDate
            intakePeriod = info.intakePeriod
            medicineVolume = info.ingredientAmount
            medicineUnit = TranslationUtil.translateUnitToLowercase(info.ingredientUnit ?: "")
            isAlarmOn = info.isAlarm

            // UI에 표시
            tvDay.text = intakeFrequency.joinToString(", ")
            tvTimeCount.text = intakeCount.joinToString(", ")
            tvMealUnit.text = mealUnit ?: "-"
            etMinutes.setText(mealTime?.toString() ?: "")
            etEatCount.setText(eatCount?.toString() ?: "")
            tvEatUnit.text = eatUnit ?: "-"
            tvStartDate.text = startDate ?: "-"
            etPeriod.setText(intakePeriod.toString()) // Null 가능성 없음
            etMedicineVolume.setText(medicineVolume.toString()) // Null 가능성 없음
            tvMedicineUnit.text = medicineUnit ?: "-"
            switchAlarm.isChecked = isAlarmOn
            updateSelectedTimes(intakeCount) // 복용 시간 Chip 추가
            updateEndDateChip()             // 복용 종료일 Chip 추가

            // 변경 불가 필드 저장
            medicineImage = info.medicineImage
        }
    }

    private fun updateSelectedTimes(selectedTimes: List<String>) {
        binding.llSelectedTimes.removeAllViews()

        selectedTimes.forEach { time ->
            val chip = CustomChip.createChip(this, time)
            binding.llSelectedTimes.addView(chip)
        }
        val dosingTextView = CustomChip.createDosingTextView(this)
        binding.llSelectedTimes.addView(dosingTextView)
    }

    private fun updateEndDateChip() {
        binding.layoutEndDateChip.removeAllViews()
        val endDate = DateConversionUtil.calculateEndDate(startDate ?: "", intakePeriod ?: 0) ?: return
        val chip = CustomChip.createChip(this, getString(R.string.seven_end_date_chip, endDate))
        binding.layoutEndDateChip.addView(chip)
    }

    private fun setupClickListeners() {
        binding.apply {
            layoutIntakeFrequency.setOnClickListener { showDaySelectionBottomSheet() }
            layoutIntakeCount.setOnClickListener { showSelectTimeBottomSheet() }
            layoutMealUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.MEAL_TIME, mealUnit) }
            layoutEatUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.DOSAGE_UNIT, eatUnit) }
            layoutStartDate.setOnClickListener { showCalendarBottomSheet() }
            // 3자리 제한 적용
            etPeriod.filters = arrayOf(InputFilter.LengthFilter(3))
            binding.etPeriod.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val inputText = s.toString().trim()
                    intakePeriod = inputText.toIntOrNull() ?: 0

                    if (intakePeriod!! > 0) {
                        updateEndDateChip()
                    } else {
                        binding.layoutEndDateChip.removeAllViews()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            layoutMedicineUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.VOLUME_UNIT, medicineUnit) }
            switchAlarm.setOnCheckedChangeListener { _, isChecked ->
                isAlarmOn = isChecked
            }
        }
    }

    private fun showDaySelectionBottomSheet() {
        val bottomSheet = DaySelectionBottomSheetFragment(
            initiallySelectedDays = intakeFrequency
        ) { selectedDays ->
            intakeFrequency = selectedDays

            val allDaysKorean = listOf(
                getString(R.string.day_sunday), getString(R.string.day_monday), getString(R.string.day_tuesday),
                getString(R.string.day_wednesday), getString(R.string.day_thursday), getString(R.string.day_friday),
                getString(R.string.day_saturday)
            )

            // 모든 요일이 선택되었을 경우 "매일" 표시, 아니면 선택한 요일 표시
            binding.tvDay.text = if (selectedDays.toSet() == allDaysKorean.toSet()) {
                getString(R.string.three_everyday)
            } else {
                selectedDays.joinToString(", ")
            }
        }
        bottomSheet.show(supportFragmentManager, "DaySelectionBottomSheet")
    }

    private fun showSelectTimeBottomSheet() {
        val bottomSheet = SelectTimeBottomSheetFragment(
            initiallySelectedTimes = intakeCount
        ) { selectedTimes ->
            intakeCount = selectedTimes
            binding.tvTimeCount.text = selectedTimes.joinToString(", ") // 텍스트뷰 업데이트
            updateSelectedTimes(selectedTimes) // Chip UI 업데이트
        }
        bottomSheet.show(supportFragmentManager, "SelectTimeBottomSheet")
    }

    private fun showCheckBottomSheet(type: BottomSheetType, selectedOption: String?) {
        val bottomSheet = CheckBottomSheetFragment.newInstance(type, selectedOption)
        bottomSheet.show(supportFragmentManager, "CheckBottomSheet")

        // 선택한 옵션이 dismiss될 때 결과 반영
        supportFragmentManager.setFragmentResultListener(
            "selectedOptionKey", this
        ) { _, bundle ->
            val selectedValue = bundle.getString("selectedOption") ?: return@setFragmentResultListener

            when (type) {
                BottomSheetType.MEAL_TIME -> {
                    mealUnit = selectedValue
                    binding.tvMealUnit.text = selectedValue // 식사 시간 반영
                }
                BottomSheetType.DOSAGE_UNIT -> {
                    eatUnit = selectedValue
                    binding.tvEatUnit.text = selectedValue // 복용 단위 반영
                }
                BottomSheetType.VOLUME_UNIT -> {
                    medicineUnit = selectedValue
                    binding.tvMedicineUnit.text = selectedValue // 약물 단위 반영
                }
            }
        }
    }

    private fun showCalendarBottomSheet() {
        val bottomSheet = CalendarBottomSheetFragment.newInstance(startDate) { selectedDate ->
            startDate = selectedDate
            binding.tvStartDate.text = selectedDate // 선택한 날짜 업데이트
            updateEndDateChip()
        }
        bottomSheet.show(supportFragmentManager, "CalendarBottomSheet")
    }

    private fun setupSaveButton() {
        binding.btnFinish.setOnClickListener {
            val updatedData = createEditMedicineInfo() // 입력된 데이터 기반으로 객체 생성

            if (updatedData != null) {
                sendUpdateRequest(updatedData)
            } else {
                Toast.makeText(this, "입력 데이터를 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createEditMedicineInfo(): MedicineEditInfo? {
        return try {
            MedicineEditInfo(
                medicineImage = medicineImage, // 변경 불가

                medicineName = binding.tvPillName.text.toString(),
                className = binding.tvClassName.text.toString(),
                entpName = binding.tvCompanyName.text.toString(),

                // 변환 적용 (한국어 → 영어)
                intakeCounts = intakeCount.mapNotNull { TranslationUtil.translateTimeToEnglish(it) }.toSet(),
                intakeFrequencys = intakeFrequency.mapNotNull { TranslationUtil.translateDayToEnglish(it) }.flatten().toSet(),
                mealUnit = mealUnit?.let { TranslationUtil.translateMealUnitToEnglish(it) },
                mealTime = binding.etMinutes.text.toString().toIntOrNull(),
                eatUnit = binding.tvEatUnit.text.toString().let { TranslationUtil.translateEatUnitToEnglish(it) ?: it },
                eatCount = binding.etEatCount.text.toString().toIntOrNull() ?: 0,
                startDate = binding.tvStartDate.text.toString(),
                intakePeriod = binding.etPeriod.text.toString().toIntOrNull() ?: 0,
                ingredientUnit = binding.tvMedicineUnit.text.toString().takeIf { it.isNotEmpty() }?.let {
                    TranslationUtil.translateUnitToUppercase(it)
                },
                ingredientAmount = binding.etMedicineVolume.text.toString().toFloatOrNull(),
                isAlarm = binding.switchAlarm.isChecked,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendUpdateRequest(request: MedicineEditInfo) {
        // JSON 변환을 위한 Gson 객체 생성
        val gson = Gson()
        val jsonRequest = gson.toJson(request)

        // 데이터 로그 출력
        Log.d("MedicineEditActivity", "Sending update request: $jsonRequest")
        //medicineEditService
        mockMedicineEditService.editMedicineInfo(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MedicineEditActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish() // 수정 완료 후 화면 종료
                } else {
                    Toast.makeText(this@MedicineEditActivity, "수정 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MedicineEditActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }
}