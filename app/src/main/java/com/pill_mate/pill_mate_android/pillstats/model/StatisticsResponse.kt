package com.pill_mate.pill_mate_android.pillstats.model

// Gson은 Kotlin의 non-null 선언을 지키지 않아서, 서버 응답 값이 비거나 형식이 달라도 앱이 죽지 않도록
// 문자열·리스트는 nullable로 받고 파싱 실패한 항목은 null로 돌려 호출하는 쪽에서 건너뛴다.

data class ResponseStatsMedicine(
    val medicineId: Long, val scheduleId: Long, val medicineName: String?, val totalIntakeDays: Long
)

data class ResponseStatsCalendar(
    val medicineName: String?, val days: List<ResponseStatsCalendarDay>?
)

data class ResponseStatsCalendarDay(
    val date: String?, val status: String?
) {
    // 서버가 "yyyy-MM-dd"로 내려주는 날짜에서 일(day)만 꺼냄. 형식이 다르면 null
    val dayOfMonth: Int? get() = date?.takeLast(2)?.toIntOrNull()?.takeIf { it in 1..31 }
    val isTaken: Boolean get() = status == STATUS_TAKEN

    private companion object {
        const val STATUS_TAKEN = "TAKEN"
    }
}

data class ResponseMedicineCountTrend(
    val diffFromLastMonth: Int, val recommendedMax: Int, val trend: List<ResponseMedicineCountTrendItem>?
)

data class ResponseMedicineCountTrendItem(
    val yearMonth: String?, val count: Int
) {
    // "yyyy-MM" → "N월". 형식이 다르면 null
    val monthLabel: String?
        get() = yearMonth?.takeLast(2)?.toIntOrNull()?.takeIf { it in 1..12 }?.let { "${it}월" }
}
