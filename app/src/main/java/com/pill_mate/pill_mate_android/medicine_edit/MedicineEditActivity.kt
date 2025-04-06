package com.pill_mate.pill_mate_android.medicine_edit

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.ServiceCreator.medicineEditService
import com.pill_mate.pill_mate_android.databinding.ActivityMedicineEditBinding
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditInfo
import com.pill_mate.pill_mate_android.medicine_edit.model.MedicineEditResponse
import com.pill_mate.pill_mate_android.medicine_registration.CalendarBottomSheetFragment
import com.pill_mate.pill_mate_android.medicine_registration.DaySelectionBottomSheetFragment
import com.pill_mate.pill_mate_android.medicine_registration.RadioButtonBottomSheetFragment
import com.pill_mate.pill_mate_android.medicine_registration.SelectTimeBottomSheetFragment
import com.pill_mate.pill_mate_android.medicine_registration.model.BottomSheetType
import com.pill_mate.pill_mate_android.schedule.AlarmSwitchDialogFragment
import com.pill_mate.pill_mate_android.util.CustomChip
import com.pill_mate.pill_mate_android.util.DateConversionUtil
import com.pill_mate.pill_mate_android.util.KeyboardUtil
import com.pill_mate.pill_mate_android.util.TranslationUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicineEditBinding
    private var scheduleId: Long? = null
    private var medicineImage: String? = null
    private var identifyNumber: String? = null
    private var medicineId: Int? = null
    private var ingredient: String? = null
    private var ingredientAmount: Int? = null
    private var wakeupTime: String? = null
    private var morningTime: String? = null
    private var lunchTime: String? = null
    private var dinnerTime: String? = null
    private var bedTime: String? = null

    // 변수 저장
    private var intakeFrequency: List<String> = emptyList()
    private var intakeCount: List<String> = emptyList()
    private var intakeTime: List<String> = emptyList()
    private var mealUnit: String? = null
    private var mealTime: Int? = null
    private var eatCount: Int? = null
    private var eatUnit: String? = null
    private var startDate: String? = null
    private var intakePeriod: Int? = null
    private var medicineVolume: Int? = null
    private var medicineUnit: String? = null
    private var isAlarmOn: Boolean = false

    private val timeOrder = listOf(
        R.string.time_empty,
        R.string.time_morning,
        R.string.time_lunch,
        R.string.time_dinner,
        R.string.time_before_sleep
    )
    private val timeOrderMap: Map<String, Int> by lazy {
        timeOrder.withIndex().associate { getString(it.value) to it.index }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleId = intent.getLongExtra("scheduleId", -1)
        Log.d("scheduleId", "$scheduleId")

        fetchInitialData()
        setupClickListeners()
        setupSaveButton()
    }

    private fun fetchInitialData() {
        if (scheduleId == null) {
            Toast.makeText(this, "약물 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        medicineEditService.getMedicineInfo(scheduleId!!).enqueue(object : Callback<MedicineEditResponse> {
            override fun onResponse(call: Call<MedicineEditResponse>, response: Response<MedicineEditResponse>) {
                if (response.isSuccessful) {
                    val rawJson = response.body()?.toString() ?: "null"
                    Log.d("MedicineEditActivityResponse", "Raw Response: $rawJson")
                    response.body()?.let { data -> updateUI(data) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "MedicineEditActivityResponse",
                        "Failed to load data: ${response.message()} - Code: ${response.code()} - ErrorBody: $errorBody"
                    )
                }
            }

            override fun onFailure(call: Call<MedicineEditResponse>, t: Throwable) {
                Log.e("MedicineEditActivityResponse", "Network Error", t)
            }
        })
    }

    private fun updateUI(data: MedicineEditResponse) {
        val info = data.result // result 내부 데이터 사용

        binding.apply {
            // 기본 정보
            Glide.with(ivImage.context)
                .load(info.medicineImage)
                .transform(RoundedCorners(8))
                .error(R.drawable.img_default)
                .into(ivImage)
            tvPillName.text = info.medicineName
            tvClassName.text = info.className
            tvCompanyName.text = info.entpName

            // 복약 정보 매핑
            intakeFrequency = info.intakeFrequencys.mapNotNull { TranslationUtil.translateDayToKorean(it) }
            intakeCount = info.intakeCounts.mapNotNull { TranslationUtil.translateTimeToKorean(it) }
            intakeTime = info.intakeTimes
            mealUnit = TranslationUtil.translateMealUnitToKorean(info.mealUnit ?: "")
            mealTime = info.mealTime
            eatCount = info.eatCount
            eatUnit = TranslationUtil.translateEatUnitToKorean(info.eatUnit)
            startDate = DateConversionUtil.formatServerDateToDisplay(info.startDate) // 변환 적용
            intakePeriod = info.intakePeriod
            medicineVolume = info.medicineVolume
            medicineUnit = TranslationUtil.translateUnitToLowercase(info.ingredientUnit ?: "")
            isAlarmOn = info.isAlarm

            // UI에 표시
            tvDay.text = TranslationUtil.translateDayToKorean(info.intakeFrequencys)
            // N회 (공복, 아침, 점심, 저녁, 취침전) 형식으로 표시
            val count = intakeCount.size
            val selectedTimesText = intakeCount.joinToString(", ")
            tvTimeCount.text = if (count > 0) {
                getString(R.string.four_selected_time, count) + " ($selectedTimesText)"
            } else {
                ""
            }
            tvMealUnit.text = listOfNotNull(mealUnit, mealTime).joinToString(" ").ifEmpty { "-" }
            etEatCount.setText(eatCount?.toString() ?: "")
            tvEatUnit.text = eatUnit ?: "-"
            tvStartDate.text = startDate ?: "-"
            etPeriod.setText(intakePeriod.toString()) // Null 가능성 없음
            etMedicineVolume.setText(medicineVolume.toString()) // Null 가능성 없음
            tvMedicineUnit.text = medicineUnit ?: "-"
            switchAlarm.isChecked = isAlarmOn
            updateEndDateChip()             // 복용 종료일 Chip 추가

            // 변경 불가 필드 저장
            medicineImage = info.medicineImage
            identifyNumber = info.identifyNumber
            medicineId = info.medicineId
            ingredient = info.ingredient
            ingredientAmount = info.ingredientAmount
            wakeupTime = info.wakeupTime
            morningTime = info.morningTime
            lunchTime = info.lunchTime
            dinnerTime = info.dinnerTime
            bedTime = info.bedTime
        } // 복약 스케줄 UI 업데이트 적용
        updateIntakeScheduleUI()
        updateFinishButtonState()
    }

    private fun updateIntakeScheduleUI() {
        val intakeCounts = intakeCount
        val intakeTimes = intakeTime

        Log.d("updateIntakeScheduleUI", "intakeCounts: $intakeCounts")
        Log.d("updateIntakeScheduleUI", "intakeTimes: $intakeTimes")

        val visibleLayouts = mutableListOf<String>()
        var hasAlarm = false

        binding.apply {
            updateIntakeTimeUI(layoutMorning, tvMorningTime, "아침", intakeCounts, intakeTimes, visibleLayouts)
            updateIntakeTimeUI(layoutLunch, tvLunchTime, "점심", intakeCounts, intakeTimes, visibleLayouts)
            updateIntakeTimeUI(layoutDinner, tvDinnerTime, "저녁", intakeCounts, intakeTimes, visibleLayouts)

            // 선택 해제된 값 `updateAlarmTimeUI()`에서 제거
            hasAlarm = listOf(
                updateAlarmTimeUI(tvLabelFasting, tvTimeFasting, "공복", intakeCounts, intakeTimes),
                updateAlarmTimeUI(tvLabelBedtime, tvTimeBedtime, "취침전", intakeCounts, intakeTimes)
            ).any { it }

            layoutAlarmTime.visibility = if (hasAlarm) View.VISIBLE else View.GONE

            lineLunch.visibility =
                if (visibleLayouts.contains("아침") && visibleLayouts.contains("점심")) View.VISIBLE else View.GONE
            lineDinner.visibility =
                if (visibleLayouts.contains("점심") && visibleLayouts.contains("저녁")) View.VISIBLE else View.GONE
        }
    }

    private fun updateAlarmTimeUI(
        labelView: TextView,
        timeView: TextView,
        intakeType: String,
        intakeCounts: List<String>,
        intakeTimes: List<String>
    ): Boolean {
        val intakeCountsList = intakeCounts.toList()
        val intakeTimesList = intakeTimes.toList()
        val index = intakeCountsList.indexOf(intakeType)

        Log.d(
            "updateAlarmTimeUI",
            "Checking intakeType: $intakeType, index: $index, intakeCountsList: $intakeCountsList, intakeTimesList: $intakeTimesList"
        )

        return if (index != -1 && index < intakeTimesList.size) {
            timeView.text = TranslationUtil.parseTimeToDisplayFormat(intakeTimesList[index])
            labelView.visibility = View.VISIBLE
            timeView.visibility = View.VISIBLE
            true
        } else { // 선택 해제된 경우 UI 숨김 처리
            Log.d("updateAlarmTimeUI", "$intakeType is removed from UI")
            labelView.visibility = View.GONE
            timeView.visibility = View.GONE
            false
        }
    }

    private fun updateIntakeTimeUI(
        layout: View,
        textView: TextView,
        intakeType: String,
        intakeCounts: List<String>,
        intakeTimes: List<String>,
        visibleLayouts: MutableList<String>
    ) {
        val intakeCountsList = intakeCounts.toList()
        val intakeTimesList = intakeTimes.toList()

        val index = intakeCountsList.indexOf(intakeType)
        Log.d(
            "updateIntakeTimeUI", "Checking intakeType: $intakeType, index: $index, intakeCountsList: $intakeCountsList"
        )

        if (index != -1 && index < intakeTimesList.size) {
            textView.text = TranslationUtil.parseTimeToDisplayFormat(intakeTimesList[index])
            layout.visibility = View.VISIBLE
            visibleLayouts.add(intakeType)
        } else {
            layout.visibility = View.GONE
        }
    }

    private fun updateIntakeTimes() {
        Log.d("updateIntakeTimes", "Before update - intakeTime: $intakeTime")

        val updatedTimes = intakeCount.mapNotNull { time ->
            val mealType = mealUnit ?: "식후"
            val adjustment = if (mealType == "식전") -(mealTime ?: 0) else (mealTime ?: 0)

            Log.d("updateIntakeTimes", "Processing time: $time, mealType: $mealType, adjustment: $adjustment")

            when (time) {
                "공복" -> wakeupTime ?: "07:00:00" // 그대로 유지
                "아침" -> DateConversionUtil.adjustTimeByMinutes(morningTime ?: "08:00:00", adjustment)
                "점심" -> DateConversionUtil.adjustTimeByMinutes(lunchTime ?: "12:00:00", adjustment)
                "저녁" -> DateConversionUtil.adjustTimeByMinutes(dinnerTime ?: "18:00:00", adjustment)
                "취침전" -> bedTime ?: "22:00:00" // 그대로 유지
                else -> null // 선택 해제된 값은 제거
            }
        }

        intakeTime = updatedTimes
        updateIntakeScheduleUI() // UI 업데이트 실행
        Log.d("updateIntakeTimes", "Updated intakeTimes: $intakeTime")
    }

    private fun updateEndDateChip() {
        val period = binding.etPeriod.text.toString().trim().toIntOrNull() ?: 0

        if (period <= 0 || startDate.isNullOrEmpty()) {
            binding.layoutEndDateChip.visibility = View.GONE
            binding.tvWarningPeriod.visibility = View.VISIBLE
            return
        }

        val endDate = DateConversionUtil.calculateEndDate(startDate!!, period)
        if (endDate.isNullOrEmpty()) {
            binding.layoutEndDateChip.visibility = View.GONE
            binding.tvWarningPeriod.visibility = View.VISIBLE
            return
        }

        binding.layoutEndDateChip.apply {
            removeAllViews()
            visibility = View.VISIBLE
            val chip = CustomChip.createChip(context, getString(R.string.seven_end_date_chip, endDate))
            addView(chip)
        }
        binding.tvWarningPeriod.visibility = View.GONE
    }

    private fun setupClickListeners() {
        binding.apply {
            binding.ivBack.setOnClickListener {
                finish() // 현재 액티비티 종료하고 이전 액티비티로 돌아가기
            }

            layoutIntakeFrequency.setOnClickListener { showDaySelectionBottomSheet() }
            layoutIntakeCount.setOnClickListener { showSelectTimeBottomSheet() }
            layoutMealUnit.setOnClickListener { showIntakeTimeBottomSheetFragment() }
            layoutAlarmTime.setOnClickListener { showAlarmTimeBottomSheetFragment() }
            layoutEatUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.DOSAGE_UNIT, eatUnit) }
            layoutStartDate.setOnClickListener { showCalendarBottomSheet() }
            layoutMedicineUnit.setOnClickListener { showCheckBottomSheet(BottomSheetType.VOLUME_UNIT, medicineUnit) }

            switchAlarm.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    showAlarmSwitchDialog()
                }
                isAlarmOn = isChecked
            }

            // etPeriod만 3자리 제한 적용 & 종료일 업데이트
            setupEditTextValidation(
                editText = etPeriod, warningTextView = tvWarningPeriod, isNumeric = true, maxLength = 3
            ) { input ->
                intakePeriod = input.toIntOrNull() ?: 0
                updateEndDateChip()
            }
            setupEditTextValidation(etEatCount, tvWarningEatCount, isNumeric = true, minValue = 1)
            setupKeyboardAction(etMedicineVolume)

            // 클릭 시 tooltip VISIBLE
            layoutMealInfoClickArea.setOnClickListener {
                binding.ivMealTooltip.visibility = View.VISIBLE
            }

            // tooltip 숨김
            layoutRoot.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN && binding.ivMealTooltip.visibility == View.VISIBLE) {
                    binding.ivMealTooltip.visibility = View.GONE
                }
                false
            }

            layoutRoot.viewTreeObserver.addOnScrollChangedListener {
                if (binding.ivMealTooltip.visibility == View.VISIBLE) {
                    binding.ivMealTooltip.visibility = View.GONE
                }
            }
        }
    }

    private fun showAlarmSwitchDialog() {
        val dialogFragment = AlarmSwitchDialogFragment()
        dialogFragment.setAlarmSwitchDialogListener(object : AlarmSwitchDialogFragment.AlarmSwitchDialogListener {
            override fun onDialogPositiveClick() {
                binding.switchAlarm.isChecked = true
                isAlarmOn = true
            }

            override fun onDialogNegativeClick() {
                isAlarmOn = false
            }
        })
        dialogFragment.show(supportFragmentManager, "AlarmSwitchDialog")
    }

    private fun setupEditTextValidation(
        editText: EditText,
        warningTextView: TextView,
        isNumeric: Boolean = false,
        maxLength: Int? = null,
        minValue: Int? = null,
        onValidInput: ((String) -> Unit)? = null
    ) {
        maxLength?.let { editText.filters = arrayOf(InputFilter.LengthFilter(it)) }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString().trim()
                val isInvalid = when {
                    inputText.isEmpty() -> true
                    isNumeric -> {
                        val value = inputText.toIntOrNull()
                        value == null || (minValue != null && value < minValue)
                    }

                    else -> false
                }

                warningTextView.visibility = if (isInvalid) View.VISIBLE else View.GONE
                editText.setBackgroundResource(if (isInvalid) R.drawable.bg_edittext_red else R.drawable.bg_selector_edittext)

                if (!isInvalid) {
                    onValidInput?.invoke(inputText)
                } else if (editText.id == R.id.et_period) {
                    binding.layoutEndDateChip.visibility = View.GONE
                }
                updateFinishButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        setupKeyboardAction(editText)
    }

    private fun setupKeyboardAction(editText: EditText) {
        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                editText.clearFocus()
                KeyboardUtil.hideKeyboard(this, editText)
                true
            } else {
                false
            }
        }
    }

    private fun showIntakeTimeBottomSheetFragment() {
        val bottomSheet =
            IntakeTimeBottomSheetFragment.newInstance(mealUnit, mealTime ?: 0) { selectedMealUnit, selectedMealTime ->
                mealUnit = selectedMealUnit
                mealTime = selectedMealTime

                // UI 업데이트: 즉시는 그대로, 나머지는 "10분", "20분" 등으로 표시
                binding.tvMealUnit.text =
                    if (selectedMealTime == 0) "$mealUnit 즉시" else "$mealUnit ${selectedMealTime}분"

                // 복약 시간 업데이트
                updateIntakeTimes()
            }
        bottomSheet.show(supportFragmentManager, "MealTimeBottomSheet")
    }

    // 공복, 취침전 시간 업데이트
    private fun showAlarmTimeBottomSheetFragment() {
        Log.d("showAlarmTimeBottomSheetFragment", "intakeCount: $intakeCount")
        Log.d("showAlarmTimeBottomSheetFragment", "intakeTime: $intakeTime")

        val fastingIndex = intakeCount.indexOf("공복")
        val bedtimeIndex = intakeCount.indexOf("취침전")

        val fastingTime = if (fastingIndex != -1 && fastingIndex < intakeTime.size) {
            intakeTime[fastingIndex]
        } else {
            ""
        }

        val bedtimeTime = if (bedtimeIndex != -1 && bedtimeIndex < intakeTime.size) {
            intakeTime[bedtimeIndex]
        } else {
            ""
        }

        val showFastingPicker = fastingIndex != -1
        val showBedtimePicker = bedtimeIndex != -1

        val bottomSheet = AlarmTimeBottomSheetFragment(
            wakeupTime = fastingTime, bedTime = bedtimeTime, onTimeSelected = { selectedWakeupTime, selectedBedTime ->
                wakeupTime = selectedWakeupTime
                bedTime = selectedBedTime
                updateIntakeTimes()
            }, showFastingPicker = showFastingPicker, showBedtimePicker = showBedtimePicker
        )

        bottomSheet.show(supportFragmentManager, "AlarmTimeBottomSheet")
    }

    private fun showDaySelectionBottomSheet() {
        val bottomSheet = DaySelectionBottomSheetFragment(
            initiallySelectedDays = intakeFrequency
        ) { selectedDays ->
            intakeFrequency = selectedDays

            val allDaysKorean = listOf(
                getString(R.string.day_sunday),
                getString(R.string.day_monday),
                getString(R.string.day_tuesday),
                getString(R.string.day_wednesday),
                getString(R.string.day_thursday),
                getString(R.string.day_friday),
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
            Log.d("showSelectTimeBottomSheet", "Selected times: $selectedTimes")

            // 기존 intakeCount에서 선택 해제된 값이 반영되도록 갱신
            intakeCount = selectedTimes.sortedBy { timeOrderMap[it] ?: Int.MAX_VALUE }
            updateSelectedTimeText()
            updateIntakeTimes() // 선택한 시간 반영 후 업데이트
        }
        bottomSheet.show(supportFragmentManager, "SelectTimeBottomSheet")
    }

    private fun updateSelectedTimeText() {
        val count = intakeCount.size

        val sortedTimes = intakeCount.sortedBy { timeOrderMap[it] ?: Int.MAX_VALUE }
        val selectedTimesText = sortedTimes.joinToString(", ") // 정렬된 선택된 시간 문자열

        binding.tvTimeCount.text = if (count > 0) {
            getString(R.string.four_selected_time, count) + " ($selectedTimesText)"
        } else {
            ""
        }
    }

    private fun showCheckBottomSheet(type: BottomSheetType, selectedOption: String?) {
        val bottomSheet = RadioButtonBottomSheetFragment.newInstance(type, selectedOption)
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

    private fun isFormValid(): Boolean {
        val periodValid = binding.etPeriod.text.toString().toIntOrNull()?.let { it > 0 } ?: false
        val eatCountValid = binding.etEatCount.text.toString().toIntOrNull()?.let { it > 0 } ?: false

        return periodValid && eatCountValid
    }

    private fun updateFinishButtonState() {
        val isValid = isFormValid()
        binding.btnFinish.isEnabled = isValid
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
                // 변경 불가
                medicineImage = medicineImage,
                identifyNumber = identifyNumber ?: "",
                medicineId = medicineId ?: 0,
                ingredient = ingredient,
                ingredientAmount = ingredientAmount,
                wakeupTime = wakeupTime,
                morningTime = morningTime,
                lunchTime = lunchTime,
                dinnerTime = dinnerTime,
                bedTime = bedTime,
                medicineName = binding.tvPillName.text.toString(),
                className = binding.tvClassName.text.toString(),
                entpName = binding.tvCompanyName.text.toString(),

                // 변환 적용 (한국어 → 영어)
                intakeCounts = intakeCount.mapNotNull { TranslationUtil.translateTimeToEnglish(it) },
                intakeTimes = intakeTime,
                intakeFrequencys = intakeFrequency.mapNotNull { TranslationUtil.translateDayToEnglish(it) }.flatten(),
                mealUnit = mealUnit?.let { TranslationUtil.translateMealUnitToEnglish(it) },
                mealTime = mealTime,
                eatUnit = binding.tvEatUnit.text.toString().let { TranslationUtil.translateEatUnitToEnglish(it) ?: it },
                eatCount = binding.etEatCount.text.toString().toIntOrNull() ?: 0,
                startDate = DateConversionUtil.toIso8601(binding.tvStartDate.text.toString()) ?: "",
                intakePeriod = binding.etPeriod.text.toString().toIntOrNull() ?: 0,
                ingredientUnit = binding.tvMedicineUnit.text.toString().takeIf { it.isNotEmpty() }?.let {
                    TranslationUtil.translateUnitToUppercase(it)
                },
                medicineVolume = binding.etMedicineVolume.text.toString().toIntOrNull(),
                isAlarm = binding.switchAlarm.isChecked,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendUpdateRequest(request: MedicineEditInfo) {
        val gson = Gson()
        val jsonRequest = gson.toJson(request)

        Log.d("sendUpdateRequest", "Requesting Update: $jsonRequest")

        medicineEditService.editMedicineInfo(scheduleId!!, request)  // itemSeq 전달 추가
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MedicineEditActivity, "수정 완료", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e(
                            "sendUpdateRequest",
                            "Response Failed: ${response.code()} - ${response.errorBody()?.string()}"
                        )
                        Toast.makeText(this@MedicineEditActivity, "수정 실패: ${response.message()}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("sendUpdateRequest", "Network Error", t)
                    Toast.makeText(this@MedicineEditActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
    }
}