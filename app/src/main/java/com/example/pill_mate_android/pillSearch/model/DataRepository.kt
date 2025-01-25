package com.example.pill_mate_android.pillSearch.model

import java.net.URI
import java.text.SimpleDateFormat

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

        return MedicineRegisterRequest(
            pharmacyName = pharmacy?.pharmacyName,
            pharmacyPhone = pharmacy?.pharmacyPhone,
            pharmacyAddress = pharmacy?.pharmacyAddress,
            hospitalName = hospital?.hospitalName,
            hospitalPhone = hospital?.hospitalPhone,
            hospitalAddress = hospital?.hospitalAddress,
            identifyNumber = medicine.identify_number,
            medicineName = medicine.medicine_name,
            ingredient = medicine.ingredient,
            ingredientUnit = "mg", // 성분 단위 (필요 시 데이터에서 계산)
            ingredientAmount = 0.5f, // 성분 양 (데이터 기반 값 설정)
            medicineImage = medicine.image?.let { URI(it) },
            entpName = medicine.entp_name,
            classname = medicine.classname,
            efficacy = medicine.efficacy,
            sideEffect = medicine.side_effect,
            caution = medicine.caution,
            storage = medicine.storage,
            medicineId = schedule.medicine_id.toLong(),
            intakeCounts = schedule.intake_count.split(",").toSet(),
            intakeFrequencys = schedule.intake_frequency.split(",").toSet(),
            mealUnit = schedule.meal_unit,
            mealTime = schedule.meal_time,
            eatUnit = schedule.eat_unit,
            eatCount = schedule.eat_count,
            startDate = SimpleDateFormat("yyyy-MM-dd").parse(schedule.start_date),
            intakePeriod = schedule.intake_period,
            medicineVolume = schedule.medicine_volume,
            isAlarm = schedule.is_alarm,
            cautionTypes = schedule.caution_types.split(",").toSet()
        )
    }
}