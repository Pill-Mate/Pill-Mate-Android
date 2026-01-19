package com.pill_mate.pill_mate_android.medicine_registration.model

import com.pill_mate.pill_mate_android.util.DateConversionUtil
import com.pill_mate.pill_mate_android.util.TranslationUtil

object DataRepository {
    var hospitalData: Hospital? = null
    var pharmacyData: Pharmacy? = null
    private var medicineData: Medicine? = null
    private var scheduleData: Schedule? = null

    // Medicine
    fun saveMedicine(medicine: Medicine) {
        medicineData = medicine
    }

    fun getMedicine(): Medicine? {
        return medicineData
    }

    fun clearMedicine() {
        medicineData = null
    }

    // Schedule
    fun saveSchedule(schedule: Schedule) {
        scheduleData = schedule
    }

    fun getSchedule(): Schedule? {
        return scheduleData
    }

    fun clearSchedule() {
        scheduleData = null
    }

    // 데이터 초기화
    fun clearAll() {
        hospitalData = null
        pharmacyData = null
        medicineData = null
        scheduleData = null
    }

    // MedicineRegisterRequest 생성
    fun createMedicineRegisterRequest(): MedicineRegisterRequest? {
        val hospital = hospitalData
        val pharmacy = pharmacyData
        val medicine = getMedicine() // null일 수 있음
        val schedule = getSchedule() ?: return null // schedule은 필수

        try {
            val formattedStartDate = DateConversionUtil.toIso8601(schedule.start_date) ?: return null

            val idNum = medicine?.identify_number
            val sourceTypeVal = if (idNum.isNullOrEmpty() || idNum == "0") "CUSTOM" else "API"
            val itemSeqVal = idNum?.toLongOrNull() ?: 0L

            val intakeCounts = schedule.intake_count
                .split(",")
                .mapNotNull { TranslationUtil.translateTimeToEnglish(it.trim()) }
                .toSet()

            val intakeFrequencies = schedule.intake_frequency
                .split(",")
                .flatMap { day ->
                    TranslationUtil.translateDayToEnglish(day.trim()) ?: emptyList()
                }
                .toSet()

            return MedicineRegisterRequest(
                // 병원, 약국 정보도 빈값이면 null로 보내고 싶다면 .ifBlank { null } 처리 (여기선 기존 유지)
                pharmacyName = pharmacy?.pharmacyName?.ifBlank { null },
                pharmacyPhone = pharmacy?.pharmacyPhone?.ifBlank { null },
                pharmacyAddress = pharmacy?.pharmacyAddress?.ifBlank { null },
                hospitalName = hospital?.hospitalName?.ifBlank { null },
                hospitalPhone = hospital?.hospitalPhone?.ifBlank { null },
                hospitalAddress = hospital?.hospitalAddress?.ifBlank { null },

                sourceType = sourceTypeVal,
                itemSeq = itemSeqVal,

                // 약 이름은 필수이므로 null 대신 schedule 이름이라도 넣음
                medicineName = if (!medicine?.medicine_name.isNullOrBlank()) medicine!!.medicine_name
                else schedule.medicine_name,

                // 값이 비어있으면("") null을 보냄
                ingredient = medicine?.ingredient?.ifBlank { null },

                ingredientUnit = if (schedule.medicine_volume == 0f && schedule.medicine_unit.isEmpty()) "SKIP"
                else TranslationUtil.translateUnitToUppercase(schedule.medicine_unit),

                ingredientAmount = if (schedule.medicine_volume == 0f && schedule.medicine_unit.isEmpty()) 0.0f
                else schedule.medicine_volume,

                // 이미지도 비어있으면 null
                medicineImage = medicine?.image?.ifBlank { null },

                // [핵심 수정] 빈 문자열("") -> null로 변환
                entpName = medicine?.entp_name?.ifBlank { null },
                classname = medicine?.classname?.ifBlank { null },
                efficacy = medicine?.efficacy?.ifBlank { null },
                sideEffect = medicine?.side_effect?.ifBlank { null },
                caution = medicine?.caution?.ifBlank { null },
                storage = medicine?.storage?.ifBlank { null },
                intakeCounts = intakeCounts,
                intakeFrequencys = intakeFrequencies,

                mealUnit = if (schedule.meal_unit.isEmpty()) null
                else TranslationUtil.translateMealUnitToEnglish(schedule.meal_unit),

                mealTime = if (schedule.meal_unit == null) null else schedule.meal_time,
                eatUnit = TranslationUtil.translateEatUnitToEnglish(schedule.eat_unit) ?: "UNKNOWN",
                eatCount = schedule.eat_count,
                startDate = formattedStartDate,
                intakePeriod = schedule.intake_period,
                medicineVolume = schedule.medicine_volume,
                isAlarm = schedule.is_alarm,
                cautionTypes = schedule.caution_types.split(",").map { it.trim() }.toSet()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}