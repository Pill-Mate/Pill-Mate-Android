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
        val medicine = getMedicine()
        val schedule = getSchedule()

        if (medicine == null || schedule == null) return null

        try {
            val formattedStartDate = DateConversionUtil.toIso8601(schedule.start_date) ?: return null
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
                pharmacyName = pharmacy?.pharmacyName ?: "",
                pharmacyPhone = pharmacy?.pharmacyPhone ?: "",
                pharmacyAddress = pharmacy?.pharmacyAddress ?: "",
                hospitalName = hospital?.hospitalName ?: "",
                hospitalPhone = hospital?.hospitalPhone ?: "",
                hospitalAddress = hospital?.hospitalAddress ?: "",
                identifyNumber = medicine.identify_number,
                medicineName = medicine.medicine_name,
                ingredient = medicine.ingredient,
                ingredientUnit = if (schedule.medicine_volume == 0f && schedule.medicine_unit.isEmpty()) "SKIP" else TranslationUtil.translateUnitToUppercase(schedule.medicine_unit),
                ingredientAmount = if (schedule.medicine_volume == 0f && schedule.medicine_unit.isEmpty()) 0.0f else schedule.medicine_volume,
                medicineImage = medicine.image ?: "",
                entpName = medicine.entp_name,
                classname = medicine.classname,
                efficacy = medicine.efficacy ?: "",
                sideEffect = medicine.side_effect ?: "",
                caution = medicine.caution ?: "",
                storage = medicine.storage ?: "",
                medicineId = schedule.medicine_id.toLong(),
                intakeCounts = intakeCounts,
                intakeFrequencys = intakeFrequencies,
                mealUnit = TranslationUtil.translateMealUnitToEnglish(schedule.meal_unit) ?: "UNKNOWN",
                mealTime = schedule.meal_time,
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