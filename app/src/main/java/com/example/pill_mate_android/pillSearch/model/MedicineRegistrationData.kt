package com.example.pill_mate_android.pillSearch.model

import java.net.URI
import java.util.Date

data class MedicineRegisterRequest(
    val pharmacyName: String?,              // 약국 이름
    val pharmacyPhone: String?,             // 약국 전화번호
    val pharmacyAddress: String?,           // 약국 주소
    val hospitalName: String?,              // 병원 이름
    val hospitalPhone: String?,             // 병원 전화번호
    val hospitalAddress: String?,           // 병원 주소
    val identifyNumber: String,             // 식별 번호
    val medicineName: String,               // 약물 이름
    val ingredient: String,                 // 성분
    val ingredientUnit: String,             // 성분 단위 (mg, ml 등)
    val ingredientAmount: Float,            // 성분 양
    val medicineImage: URI?,                // 약물 이미지 URL
    val entpName: String,                   // 제약회사 이름
    val classname: String,                  // 약물 분류명
    val efficacy: String?,                  // 효능
    val sideEffect: String?,                // 부작용
    val caution: String?,                   // 주의사항
    val storage: String?,                   // 보관 방법
    val medicineId: Long,                   // 약물 ID
    val intakeCounts: Set<String>,          // 복용 횟수 (아침/점심/저녁)
    val intakeFrequencys: Set<String>,      // 복용 요일 (월/화/수)
    val mealUnit: String,                   // 식사 단위 (예: 식전/식후)
    val mealTime: Int,                      // 식사 시간 (분 단위)
    val eatUnit: String,                    // 1회 투여 단위 (예: ml, 정 등)
    val eatCount: Int,                      // 1회 투여량
    val startDate: Date,                    // 복용 시작일
    val intakePeriod: Int,                  // 복용 기간
    val medicineVolume: Float,              // 약물 용량
    val isAlarm: Boolean,                   // 알람 여부
    val cautionTypes: Set<String>?          // 주의사항 타입
)

data class Hospital(
    val hospitalName: String,
    val hospitalAddress: String,
    val hospitalPhone: String
)

data class Pharmacy(
    val pharmacyName: String,
    val pharmacyAddress: String,
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
    val medicine_unit: String = "",     // 1회 투여 용량 단위
    val medicine_volume: Float = 0.0f,  // 1회 투여 용량 (0.45mg)
    val is_alarm: Boolean = false,      // 알람 여부
    val caution_types: String = ""      // 주의 사항 (복용주의 타입)
)