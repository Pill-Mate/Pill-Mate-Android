package com.pill_mate.pill_mate_android.medicine_edit.model

data class MedicineEditResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: MedicineEditInfo
)

data class MedicineEditInfo(
    val identifyNumber: String, // 약물 식별 번호
    val medicineId: Int, // 약물 ID
    val medicineName: String, // 약물명
    val medicineImage: String?, // 약물 이미지 URL
    val entpName: String, // 제약회사명
    val className: String, // 약물 분류명
    val ingredient: String?, // 주성분 설명
    val ingredientAmount: Int?, // 주성분 양

    val intakeCounts: Set<String>, // 복용 시간 라벨 (아침, 저녁 등)
    val intakeTimes: Set<String>, // 복용 시간 리스트 ("08:30:00", "21:00:00")
    val intakeFrequencys: Set<String>, // 복용 요일 리스트

    val wakeupTime: String?, // 기상 시간
    val morningTime: String?, // 아침 시간
    val lunchTime: String?, // 점심 시간
    val dinnerTime: String?, // 저녁 시간
    val bedTime: String?, // 취침 시간

    val mealUnit: String?, // 식사 전/후 구분 (MEALBEFORE, MEALAFTER 등)
    val mealTime: Int?, // 식사 전후 몇 분 후 복용

    val eatUnit: String, // 복용 단위 (정, ML 등)
    val eatCount: Int, // 1회 투여량

    val startDate: String, // 복용 시작 날짜 (ISO 8601 형식)
    val intakePeriod: Int, // 복용 기간 (일 단위)

    val medicineVolume: Int?, // 약물 용량
    val ingredientUnit: String?, // 성분 단위 (MG, ML 등)
    val isAlarm: Boolean // 알람 설정 여부
)