package com.example.pill_mate_android.pillSearch.model

object DataRepository {
    var hospitalData: Hospital? = null
    var pharmacyData: Pharmacy? = null
    private var medicineData: Medicine? = null
    private var scheduleData: Schedule? = null

    // Medicine 저장
    fun saveMedicine(medicine: Medicine) {
        medicineData = medicine
    }

    // Medicine 가져오기
    fun getMedicine(): Medicine? {
        return medicineData
    }

    // Medicine 초기화
    fun clearMedicine() {
        medicineData = null
    }

    // Schedule 저장
    fun saveSchedule(schedule: Schedule) {
        scheduleData = schedule
    }

    // Schedule 가져오기
    fun getSchedule(): Schedule? {
        return scheduleData
    }

    // Schedule 초기화
    fun clearSchedule() {
        scheduleData = null
    }
}