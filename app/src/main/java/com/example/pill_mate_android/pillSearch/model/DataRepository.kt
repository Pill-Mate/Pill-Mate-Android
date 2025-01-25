package com.example.pill_mate_android.pillSearch.model

import com.example.pill_mate_android.pillSearch.util.DateConversionUtil
import com.example.pill_mate_android.pillSearch.util.TimeTranslationUtil

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
        val medicine = getMedicine()
        val schedule = getSchedule()

        if (medicine == null || schedule == null) return null

        try {
            val formattedStartDate = DateConversionUtil.toIso8601(schedule.start_date) ?: return null
            val intakeCounts = schedule.intake_count
                .split(",")
                .mapNotNull { TimeTranslationUtil.translateTimeToEnglish(it.trim()) }
                .toSet()

            val intakeFrequencies = schedule.intake_frequency
                .split(",")
                .flatMap { day ->
                    TimeTranslationUtil.translateDayToEnglish(day.trim()) ?: emptyList()
                }
                .toSet()

            return MedicineRegisterRequest(
                pharmacyName = pharmacy?.pharmacyName ?: "string",
                pharmacyPhone = pharmacy?.pharmacyPhone ?: "string",
                pharmacyAddress = pharmacy?.pharmacyAddress ?: "string",
                hospitalName = hospital?.hospitalName ?: "string",
                hospitalPhone = hospital?.hospitalPhone ?: "string",
                hospitalAddress = hospital?.hospitalAddress ?: "string",
                identifyNumber = medicine.identify_number,
                medicineName = medicine.medicine_name,
                ingredient = medicine.ingredient,
                ingredientUnit = "MG",
                ingredientAmount = 0.0f,
                medicineImage = medicine.image ?: "string",
                entpName = medicine.entp_name,
                classname = medicine.classname,
                efficacy = medicine.efficacy ?: "string",
                sideEffect = medicine.side_effect ?: "string",
                caution = medicine.caution ?: "string",
                storage = medicine.storage ?: "string",
                medicineId = schedule.medicine_id.toLong(),
                intakeCounts = intakeCounts,
                intakeFrequencys = intakeFrequencies,
                mealUnit = TimeTranslationUtil.translateMealUnitToEnglish(schedule.meal_unit) ?: "UNKNOWN",
                mealTime = schedule.meal_time,
                eatUnit = TimeTranslationUtil.translateEatUnitToEnglish(schedule.eat_unit) ?: "UNKNOWN",
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