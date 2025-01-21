package com.example.pill_mate_android.pillSearch.presenter

import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem

interface StepTwoPresenter {
    fun onPillSelected(pillItem: PillIdntfcItem)  // 약 이름 선택 처리
    fun handleTextChange(input: String)   // EditText의 텍스트 변경 처리
}