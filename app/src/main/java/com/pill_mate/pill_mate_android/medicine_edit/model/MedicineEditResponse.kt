package com.pill_mate.pill_mate_android.medicine_edit.model

data class MedicineEditResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: MedicineEditInfo
)

data class MedicineEditInfo(
    val medicineName: String, // 약물명
    val medicineImage: String?, // URI 대신 String으로 변경
    val entpName: String, // 제약회사
    val className: String, // 약물 분류명

    val intakeCounts: Set<String>, // 복용 시간 라벨 아점저 등
    val intakeTimes: Set<String>, // 복용 시간 19시
    val intakeFrequencys: Set<String>, // 복용 요일
    val mealUnit: String?, // MEALBEFORE, MEALAFTER 등
    val mealTime: Int?, // 식전후 몇분
    val eatUnit: String, // JUNG, ML 등
    val eatCount: Int, //1회 투여량
    val startDate: String, // ISO 8601 형식
    val intakePeriod: Int, //복용기간
    val ingredientUnit: String?, // MG, ML 등 1회 투여 용량 단위
    val ingredientAmount: Float?, // 1회 투여 용량 (0.45)
    val isAlarm: Boolean,
)