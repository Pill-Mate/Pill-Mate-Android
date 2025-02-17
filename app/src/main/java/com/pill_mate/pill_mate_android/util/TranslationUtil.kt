package com.pill_mate.pill_mate_android.util

object TranslationUtil {
    // 시간대
    private val timeToEnglishMap = mapOf(
        "공복" to "EMPTY",
        "아침" to "MORNING",
        "점심" to "LUNCH",
        "저녁" to "DINNER",
        "취침전" to "SLEEP"
    )
    private val timeToKoreanMap = timeToEnglishMap.entries.associate { it.value to it.key }

    // 요일
    private val dayToEnglishMap = mapOf(
        "일" to "SUNDAY",
        "월" to "MONDAY",
        "화" to "TUESDAY",
        "수" to "WEDNESDAY",
        "목" to "THURSDAY",
        "금" to "FRIDAY",
        "토" to "SATURDAY",
        "매일" to listOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")
    )
    private val dayToKoreanMap = dayToEnglishMap.entries
        .filter { it.value !is List<*> }
        .associate { it.value as String to it.key }

    // 투약 단위
    private val eatUnitToEnglishMap = mapOf(
        "정(개)" to "JUNG",
        "캡슐" to "CAPSULE",
        "ml" to "ML",
        "포" to "POH",
        "주사" to "JUSA"
    )
    private val eatUnitToKoreanMap = eatUnitToEnglishMap.entries.associate { it.value to it.key }

    private val unitToUppercaseMap = mapOf(
        "mg" to "MG",
        "mcg" to "MCG",
        "g" to "G",
        "ml" to "ML"
    )
    private val unitToLowercaseMap = unitToUppercaseMap.entries.associate { it.value to it.key }

    // 식사 단위
    private val mealUnitToEnglishMap = mapOf(
        "식전" to "MEALBEFORE",
        "식후" to "MEALAFTER"
    )
    private val mealUnitToKoreanMap = mealUnitToEnglishMap.entries.associate { it.value to it.key }

    // 변환 함수: 시간대
    fun translateTimeToEnglish(korean: String) = timeToEnglishMap[korean]
    fun translateTimeToKorean(english: String) = timeToKoreanMap[english]

    // 변환 함수: 요일
    fun translateDayToEnglish(korean: String): List<String>? {
        val result = dayToEnglishMap[korean]
        return if (result is String) listOf(result) else result as? List<String>
    }
    fun translateDayToKorean(english: String) = dayToKoreanMap[english]

    // 변환 함수: 투약 단위
    fun translateEatUnitToEnglish(korean: String) = eatUnitToEnglishMap[korean]
    fun translateEatUnitToKorean(english: String) = eatUnitToKoreanMap[english]

    // 변환 함수: 1회 투여 용량 단위 (대소문자 변환)
    fun translateUnitToUppercase(unit: String): String {
        return unitToUppercaseMap[unit.lowercase()] ?: unit.uppercase()
    }
    fun translateUnitToLowercase(unit: String): String {
        return unitToLowercaseMap[unit.uppercase()] ?: unit.lowercase()
    }

    // 변환 함수: 식사 단위
    fun translateMealUnitToEnglish(korean: String) = mealUnitToEnglishMap[korean]
    fun translateMealUnitToKorean(english: String) = mealUnitToKoreanMap[english]
}