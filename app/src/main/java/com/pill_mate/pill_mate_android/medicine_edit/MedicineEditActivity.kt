package com.pill_mate.pill_mate_android.medicine_edit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pill_mate.pill_mate_android.ServiceCreator.medicineEditService
import com.pill_mate.pill_mate_android.databinding.ActivityMedicineEditBinding
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditInfo
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditResponse
import com.pill_mate.pill_mate_android.medicine_registration.*
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
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

        medicineEditService.getMedicineInfo(itemSeq!!).enqueue(object :
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
            intakeFrequency = info.intakeFrequencys.toList()
            intakeCount = info.intakeCounts.toList()
            mealUnit = info.mealUnit
            mealTime = info.mealTime
            eatCount = info.eatCount
            eatUnit = info.eatUnit
            startDate = info.startDate
            intakePeriod = info.intakePeriod
            medicineVolume = info.ingredientAmount
            medicineUnit = info.ingredientUnit

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

            // 변경 불가 필드 저장
            medicineImage = data.result.medicineImage
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            layoutIntakeFrequency.setOnClickListener { showDaySelectionBottomSheet() }
            layoutIntakeCount.setOnClickListener { showSelectTimeBottomSheet() }
            layoutMealUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.MEAL_TIME, mealUnit) }
            layoutEatUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.DOSAGE_UNIT, eatUnit) }
            layoutStartDate.setOnClickListener { showCalendarBottomSheet() }
            layoutMedicineUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.VOLUME_UNIT, medicineUnit) }
        }
    }

    private fun showDaySelectionBottomSheet() {
        val bottomSheet = DaySelectionBottomSheetFragment(
            initiallySelectedDays = intakeFrequency
        ) { selectedDays ->
            intakeFrequency = selectedDays
        }
        bottomSheet.show(supportFragmentManager, "DaySelectionBottomSheet")
    }

    private fun showSelectTimeBottomSheet() {
        val bottomSheet = SelectTimeBottomSheetFragment(
            initiallySelectedTimes = intakeCount
        ) { selectedTimes ->
            intakeCount = selectedTimes
        }
        bottomSheet.show(supportFragmentManager, "SelectTimeBottomSheet")
    }

    private fun showCheckBottomSheet(type: BottomSheetType, selectedOption: String?) {
        val bottomSheet = CheckBottomSheetFragment.newInstance(type, selectedOption)
        bottomSheet.show(supportFragmentManager, "CheckBottomSheet")
    }

    private fun showCalendarBottomSheet() {
        val bottomSheet = CalendarBottomSheetFragment.newInstance(startDate) { selectedDate ->
            startDate = selectedDate
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

                intakeCounts = intakeCount.toSet(),
                intakeFrequencys = intakeFrequency.toSet(),
                mealUnit = mealUnit,
                mealTime = binding.etMinutes.text.toString().toIntOrNull(),
                eatUnit = binding.tvEatUnit.text.toString(),
                eatCount = binding.etEatCount.text.toString().toIntOrNull() ?: 0,
                startDate = binding.tvStartDate.text.toString(),
                intakePeriod = binding.etPeriod.text.toString().toIntOrNull() ?: 0,
                ingredientUnit = binding.tvMedicineUnit.text.toString().takeIf { it.isNotEmpty() },
                ingredientAmount = binding.etMedicineVolume.text.toString().toFloatOrNull(),
                isAlarm = binding.switchAlarm.isChecked,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendUpdateRequest(request: MedicineEditInfo) {
        medicineEditService.editMedicineInfo(request).enqueue(object : Callback<Void> {
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