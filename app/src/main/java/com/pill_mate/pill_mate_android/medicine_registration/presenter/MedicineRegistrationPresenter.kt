package com.pill_mate.pill_mate_android.medicine_registration.presenter

import android.util.Log
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.Schedule
import com.pill_mate.pill_mate_android.util.DateConversionUtil
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationView
import com.pill_mate.pill_mate_android.medicine_registration.RegistrationData

class MedicineRegistrationPresenter(
    private val repository: DataRepository,
    private val view: MedicineRegistrationView
) {
    private var currentStep = 1

    private fun getDisplayItems(): List<RegistrationData> {
        val schedule = repository.getSchedule() ?: Schedule()

        return listOf(
            RegistrationData("약물명", schedule.medicine_name.takeIf { it.isNotEmpty() && currentStep > 1 } ?: ""),
            RegistrationData("요일", schedule.intake_frequency.takeIf { it.isNotEmpty() && currentStep > 2 } ?: ""),
            RegistrationData(
                "횟수",
                schedule.intake_count?.takeIf { it.isNotEmpty() && currentStep > 3 }?.let {
                    val times = it.split(",").map { time -> time.trim() }
                    if (times.isNotEmpty()) "${times.size}회(${times.joinToString(",")})" else ""
                } ?: ""
            ),
            RegistrationData(
                "시간대",
                if (schedule.meal_time > 0 && currentStep > 4) "${schedule.meal_unit} ${schedule.meal_time}분" else ""
            ),
            RegistrationData(
                "투약량",
                if (schedule.eat_count > 0 && currentStep > 5) "${schedule.eat_count}${schedule.eat_unit}" else ""
            ),
            RegistrationData(
                "복약기간",
                if (currentStep > 6) calculateIntakePeriod(schedule) else ""
            ),
            RegistrationData(
                "투여용량",
                if (schedule.medicine_volume > 0 && currentStep > 7) "${schedule.medicine_volume}${schedule.medicine_unit}" else ""
            )
        ).filter { it.data.isNotEmpty() }
    }

    private fun calculateIntakePeriod(schedule: Schedule): String {
        return DateConversionUtil.calculateIntakePeriod(schedule.start_date, schedule.intake_period)
    }

    fun getSelectedTimes(): List<String> {
        val schedule = repository.getSchedule() ?: Schedule()
        return schedule.intake_count?.split(",")?.map { it.trim() } ?: emptyList()
    }

    fun updateSchedule(update: (Schedule) -> Schedule) {
        val currentSchedule = repository.getSchedule() ?: Schedule()
        Log.d("MedicineRegistrationPresenter", "Current schedule before update: $currentSchedule")
        val updatedSchedule = update(currentSchedule)
        repository.saveSchedule(updatedSchedule)
        Log.d("MedicineRegistrationPresenter", "Updated schedule saved: ${repository.getSchedule()}")
    }

    fun updateView(step: Int) {
        currentStep = step
        clearDataForStep(step) // 현재 Step을 초기화하여 한 스텝 밀리는 문제 해결
        view.updateRecyclerView(getDisplayItems()) // UI 업데이트
    }


    fun clearDataForStep(step: Int) {
        val currentSchedule = repository.getSchedule() ?: Schedule()

        val updatedSchedule = currentSchedule.copy(
            intake_frequency = if (step == 2) "" else currentSchedule.intake_frequency,
            intake_count = if (step == 3) "" else currentSchedule.intake_count,
            meal_time = if (step == 4) 0 else currentSchedule.meal_time,
            eat_count = if (step == 5) 0 else currentSchedule.eat_count,
            intake_period = if (step == 6) 0 else currentSchedule.intake_period,
            medicine_volume = if (step == 7) 0f else currentSchedule.medicine_volume
        )

        repository.saveSchedule(updatedSchedule)
        view.updateRecyclerView(getDisplayItems()) // UI 업데이트
    }
}