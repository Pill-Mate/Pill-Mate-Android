package com.pill_mate.pill_mate_android.ui.pilledit

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    // 복용중/복용중지 아이템 내, 날짜 형식 변환 함수
    fun formatPeriod(startDate: String, endDate: String, intakePeriod: Int): String {
        val formattedStartDate = formatDate(startDate, includeYear = true) // "2025.01.01"
        val formattedEndDate = formatDate(endDate, includeYear = false) // "02.01"
        return "$formattedStartDate~$formattedEndDate · ${intakePeriod}일"
    }

    private fun formatDate(date: String, includeYear: Boolean): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val yearFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val monthDayFormat = SimpleDateFormat("MM.dd", Locale.getDefault())

            val parsedDate = inputFormat.parse(date)
            parsedDate?.let {
                if (includeYear) yearFormat.format(it) else monthDayFormat.format(it)
            } ?: date
        } catch (e: Exception) {
            date // 변환 실패 시 원래 날짜 반환
        }
    }
}