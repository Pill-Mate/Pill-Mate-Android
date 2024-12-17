package com.example.pill_mate_android.pillSearch.presenter

import android.util.Log
import com.example.pill_mate_android.pillSearch.model.MedicineRegistrationRepository
import com.example.pill_mate_android.pillSearch.model.Schedule
import com.example.pill_mate_android.pillSearch.view.MedicineRegistrationView
import com.example.pill_mate_android.pillSearch.view.RegistrationData

class MedicineRegistrationPresenter(
    private val repository: MedicineRegistrationRepository,
    private val view: MedicineRegistrationView // View와의 연결
) {

    // 항목 순서 고정 및 null, 빈 값, 0 값 숨김
    fun getDisplayItems(): List<RegistrationData> {
        val schedule = repository.getSchedule() ?: Schedule()

        return listOf(
            RegistrationData("약물명", schedule.medicine_name.takeIf { it.isNotEmpty() } ?: ""),
            RegistrationData("요일", schedule.intake_frequency.takeIf { it.isNotEmpty() } ?: ""),
            RegistrationData(
                "횟수",
                schedule.intake_count?.takeIf { it.isNotEmpty() }?.let {
                    val times = it.split(",").map { time -> time.trim() }
                    if (times.isNotEmpty()) "${times.size}회(${times.joinToString(",")})" else ""
                } ?: "" // 빈 값일 경우 횟수를 표시하지 않음
            ),
            RegistrationData(
                "시간대",
                if (schedule.meal_time > 0) "${schedule.meal_unit} ${schedule.meal_time}분" else ""
            ),
            RegistrationData(
                "투약량",
                if (schedule.eat_count > 0) "${schedule.eat_count}${schedule.eat_unit}" else ""
            ),
            RegistrationData(
                "복약기간",
                if (schedule.intake_period > 0) "${schedule.start_date} ~ ${schedule.intake_period}일" else ""
            ),
            RegistrationData(
                "투여용량",
                if (schedule.medicine_volume > 0) "${schedule.medicine_volume}${schedule.medicine_unit}" else ""
            )
        ).filter { it.data.isNotEmpty() }
    }

    // Schedule 데이터를 업데이트하고 View에 알림
    fun updateSchedule(update: (Schedule) -> Schedule) {
        val currentSchedule = repository.getSchedule() ?: Schedule()
        val updatedSchedule = update(currentSchedule)
        repository.saveSchedule(updatedSchedule)

        // 디버그 로그 추가
        Log.d("MedicineRegistrationPresenter", "Updated schedule: $updatedSchedule")

        // View에 RecyclerView 업데이트 명령
        val displayItems = getDisplayItems()
        view.updateRecyclerView(displayItems) // View에 데이터 갱신 요청
    }
}