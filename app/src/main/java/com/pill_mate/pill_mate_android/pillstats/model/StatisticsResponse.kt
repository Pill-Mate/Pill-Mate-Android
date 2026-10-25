package com.pill_mate.pill_mate_android.pillstats.model

data class ResponseStatsMedicine(
    val medicineId: Long, val scheduleId: Long, val medicineName: String, val totalIntakeDays: Long
)

data class ResponseStatsCalendar(
    val medicineName: String, val days: List<ResponseStatsCalendarDay>
)

data class ResponseStatsCalendarDay(
    val date: String, val status: String
) {
    // 서버가 "yyyy-MM-dd"로 내려주는 날짜에서 일(day)만 꺼냄
    val dayOfMonth: Int get() = date.takeLast(2).toInt()
    val isTaken: Boolean get() = status == STATUS_TAKEN

    private companion object {
        const val STATUS_TAKEN = "TAKEN"
    }
}

data class ResponseMedicineCountTrend(
    val diffFromLastMonth: Int, val recommendedMax: Int, val trend: List<ResponseMedicineCountTrendItem>
)

data class ResponseMedicineCountTrendItem(
    val yearMonth: String, val count: Int
) {
    // "yyyy-MM" → "N월"
    val monthLabel: String get() = "${yearMonth.takeLast(2).toInt()}월"
}
