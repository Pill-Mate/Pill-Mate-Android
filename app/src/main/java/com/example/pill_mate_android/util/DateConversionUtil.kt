package com.example.pill_mate_android.util

import java.text.SimpleDateFormat
import java.util.*

object DateConversionUtil {
    private const val INPUT_DATE_FORMAT = "yyyy.MM.dd"
    private const val OUTPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DISPLAY_DATE_FORMAT = "yyyy.MM.dd"

    // 날짜 문자열을 `yyyy.MM.dd` 형식에서 ISO 8601 형식으로 변환
    fun toIso8601(dateString: String): String? {
        return try {
            val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // `Date` 객체를 `yyyy.MM.dd` 형식으로 변환
    fun formatToDisplayDate(date: Date): String {
        val displayFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
        return displayFormat.format(date)
    }

    // 시작 날짜(`yyyy.MM.dd`)와 지속 기간(일)을 기준으로 종료 날짜 계산
    fun calculateEndDate(startDate: String, duration: Int): String? {
        return try {
            val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
            val date = inputFormat.parse(startDate)
            val calendar = Calendar.getInstance().apply {
                time = date!!
                add(Calendar.DATE, duration - 1)
            }
            formatToDisplayDate(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 복약 기간 텍스트 생성
    fun calculateIntakePeriod(startDate: String, duration: Int): String {
        val endDate = calculateEndDate(startDate, duration)
        return if (endDate != null) {
            "$startDate ~ $endDate(${duration}일)"
        } else {
            ""
        }
    }
}