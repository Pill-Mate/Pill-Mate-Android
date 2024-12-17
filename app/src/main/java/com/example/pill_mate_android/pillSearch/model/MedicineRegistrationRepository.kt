package com.example.pill_mate_android.pillSearch.model

class MedicineRegistrationRepository {
    private var schedule: Schedule? = null

    // Schedule 데이터를 저장
    fun saveSchedule(newSchedule: Schedule) {
        schedule = newSchedule
    }

    // Schedule 데이터를 반환
    fun getSchedule(): Schedule? {
        return schedule
    }

    // Schedule 데이터를 초기화
    fun clearSchedule() {
        schedule = null
    }
}