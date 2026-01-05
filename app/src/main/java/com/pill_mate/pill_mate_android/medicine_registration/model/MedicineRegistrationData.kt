package com.pill_mate.pill_mate_android.medicine_registration.model

data class MedicineRegisterRequest(
    val pharmacyName: String?,
    val pharmacyPhone: String?,
    val pharmacyAddress: String?,
    val hospitalName: String?,
    val hospitalPhone: String?,
    val hospitalAddress: String?,
    val sourceType: String,      // [추가] (CUSTOM은 직접입력, API는 기존 등록)
    val itemSeq: Long?,
    val medicineName: String?,
    val ingredient: String?,
    val ingredientUnit: String?, // MG, ML 등
    val ingredientAmount: Float?,
    val medicineImage: String?, // URI 대신 String으로 변경
    val entpName: String?,
    val classname: String?,
    val efficacy: String?,
    val sideEffect: String?,
    val caution: String?,
    val storage: String?,
    val medicineId: Long?,
    val intakeCounts: Set<String>, // 복용 시간
    val intakeFrequencys: Set<String>, // 복용 요일
    val mealUnit: String?, // MEALBEFORE, MEALAFTER 등
    val mealTime: Int?,
    val eatUnit: String, // JUNG, ML 등
    val eatCount: Int,
    val startDate: String, // ISO 8601 형식
    val intakePeriod: Int,
    val medicineVolume: Float,
    val isAlarm: Boolean,
    val cautionTypes: Set<String>?
)

data class Hospital(
    val hospitalName: String = "",
    val hospitalAddress: String = "",
    val hospitalPhone: String = ""
)

data class Pharmacy(
    val pharmacyName: String = "",
    val pharmacyAddress: String = "",
    val pharmacyPhone: String = ""
)

data class Medicine(
    val identify_number: String, // 식별 번호 ///
    val medicine_name: String,   // 약물 이름 ///
    val ingredient: String,      // 성분
    val image: String?,          // 약물 이미지 URL
    val classname: String,       // 분류명 ///
    val efficacy: String,        // 효능
    val side_effect: String,     // 부작용
    val caution: String,         // 주의사항
    val storage: String,         // 보관 방법
    val entp_name: String        // 제약회사
)

data class Schedule(
    val medicine_name: String = "",      // 약물 이름
    val medicine_id: Int = 0,            // 약물 ID
    val intake_frequency: String = "",  // 복용 요일
    val intake_count: String = "",      // 복용 횟수 (아침/점심/저녁)
    val meal_unit: String = "",         // 식사 선택 (식전/식후)
    val meal_time: Int = 0,             // 식후 시간 (분 단위)
    val eat_unit: String = "",          // 1회 투여 단위 (ml, 정, 캡슐 등)
    val eat_count: Int = 0,             // 1회 투여량
    val start_date: String = "",        // 복용 시작일 (yyyy-MM-dd)
    val intake_period: Int = 0,         // 복용 기간 (일 단위)
    val medicine_unit: String = "SKIP",     // 1회 투여 용량 단위
    val medicine_volume: Float = 0.0f,  // 1회 투여 용량 (0.45mg)
    val is_alarm: Boolean = true,      // 알람 여부
    val caution_types: String = ""      // 주의 사항 (복용주의 타입)
)