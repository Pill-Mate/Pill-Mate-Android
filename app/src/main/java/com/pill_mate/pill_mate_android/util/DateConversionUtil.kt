package com.pill_mate.pill_mate_android.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateConversionUtil {
    private const val INPUT_DATE_FORMAT = "yyyy.MM.dd"
    private const val OUTPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // 서버 설정과 동일
    private const val DISPLAY_DATE_FORMAT = "yyyy.MM.dd"
    private const val SERVER_DATE_FORMAT = "yyyy-MM-dd"

    // 날짜 문자열을 `yyyy.MM.dd` 형식에서 ISO 8601 형식 (KST) 으로 변환
    fun toIso8601(dateString: String): String? {
        return try {
            val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul") // 입력은 KST 기준
            }
            val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul") // 출력도 KST 기준
            }
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // `Date` 객체를 `yyyy.MM.dd` 형식으로 변환
    private fun formatToDisplayDate(date: Date): String {
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

    // 현재 날짜를 yyyy.MM.dd 형식으로 반환
    fun getCurrentDate(): String {
        return try {
            val currentDate = Date()
            val displayFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            displayFormat.format(currentDate)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // 서버 날짜 형식("yyyy-MM-dd")을 "yyyy.MM.dd" 형식으로 변환
    fun formatServerDateToDisplay(dateString: String?): String {
        return try {
            if (dateString.isNullOrEmpty()) return "-"
            val inputFormat = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            "-"
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

    fun adjustTimeByMinutes(timeString: String, minutes: Int): String {
        return try {
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = timeFormat.parse(timeString) ?: return timeString

            val calendar = Calendar.getInstance().apply {
                time = date
                add(Calendar.MINUTE, minutes)
            }

            val result = timeFormat.format(calendar.time)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            timeString // 오류 발생 시 원래 값 반환
        }
    }

    // 서버 → UI 시간 변환 ("08:00:00" → "오전 08:00")
    fun parseTimeToDisplayFormat(time: String): String {
        return try {
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val isAM = hour < 12
            val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val period = if (isAM) "오전" else "오후"

            val formattedHourStr = formattedHour.toString().padStart(2, '0')
            val formattedMinuteStr = minute.toString().padStart(2, '0')

            "$period $formattedHourStr:$formattedMinuteStr"
        } catch (e: Exception) {
            e.printStackTrace()
            "오전 00:00"
        }
    }
}