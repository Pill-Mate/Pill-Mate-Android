package com.example.pill_mate_android.pillSearch.presenter

import android.util.Log
import com.example.pill_mate_android.pillSearch.model.MedicineRegistrationRepository
import com.example.pill_mate_android.pillSearch.model.Schedule
import com.example.pill_mate_android.pillSearch.view.MedicineRegistrationView
import com.example.pill_mate_android.pillSearch.view.RegistrationData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MedicineRegistrationPresenter(
    private val repository: MedicineRegistrationRepository,
    private val view: MedicineRegistrationView // View와의 연결
) {

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
                } ?: ""
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
                calculateIntakePeriod(schedule)
            ),
            RegistrationData(
                "투여용량",
                if (schedule.medicine_volume > 0) "${schedule.medicine_volume}${schedule.medicine_unit}" else ""
            )
        ).filter { it.data.isNotEmpty() }
    }

    private fun calculateIntakePeriod(schedule: Schedule): String {
        if (schedule.start_date.isEmpty() || schedule.intake_period <= 0) return ""

        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val startDate = dateFormat.parse(schedule.start_date) ?: return ""

        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DATE, schedule.intake_period - 1)

        val endDate = dateFormat.format(calendar.time)
        return "${schedule.start_date} ~ $endDate(${schedule.intake_period}일)"
    }

    fun getSelectedTimes(): List<String> {
        val schedule = repository.getSchedule() ?: Schedule()
        return schedule.intake_count?.split(",")?.map { it.trim() } ?: emptyList()
    }

    fun updateSchedule(update: (Schedule) -> Schedule) {
        val currentSchedule = repository.getSchedule() ?: Schedule()
        val updatedSchedule = update(currentSchedule)
        repository.saveSchedule(updatedSchedule)

        Log.d("MedicineRegistrationPresenter", "Updated schedule: $updatedSchedule")
    }

    fun updateView() {
        val displayItems = getDisplayItems()
        view.updateRecyclerView(displayItems)
    }
}