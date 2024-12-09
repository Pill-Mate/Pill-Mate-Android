package com.example.pill_mate_android.pillSearch.model

data class Hospital(
    val hospitalName: String,
    val hospitalPhone: String
)

data class Pharmacy(
    val pharmacyName: String,
    val pharmacyPhone: String
)

data class Medicine(
    val identify_number: String, // 식별 번호
    val medicine_name: String,   // 약물 이름
    val ingredient: String,      // 성분
    val image: String?,          // 약물 이미지 URL
    val classname: String,       // 분류명
    val efficacy: String,        // 효능
    val side_effect: String,     // 부작용
    val caution: String,         // 주의사항
    val storage: String          // 보관 방법
)

data class Schedule(
    val medicine_id: Int,         // 약물 ID
    val intake_frequency: String, // 복용 요일
    val intake_count: String,     // 복용 횟수 (아침/점심/저녁)
    val meal_unit: String,        // 식사 선택 (식전/식후)
    val meal_time: Int,           // 식후 시간 (분 단위)
    val eat_unit: String,         // 1회 투여 단위 (ml, 정, 캡슐 등)
    val eat_count: Int,           // 1회 투여량
    val start_date: String,       // 복용 시작일 (yyyy-MM-dd)
    val intake_period: Int,       // 복용 기간 (일 단위)
    val medicine_unit: String,    // 1회 투여 용량 단위
    val medicine_volume: Float,   // 1회 투여 용량 (0.45mg)
    val is_alarm: Boolean,        // 알람 여부
    val caution_types: String     // 주의 사항 (복용주의 타입)
)