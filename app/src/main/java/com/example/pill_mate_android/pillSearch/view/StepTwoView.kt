package com.example.pill_mate_android.pillSearch.view

interface StepTwoView {
    fun updatePillName(pillName: String)  // 선택된 약 이름 업데이트
    fun updateButtonState(isEnabled: Boolean)  // 버튼 상태 업데이트
}