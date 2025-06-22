package com.pill_mate.pill_mate_android.search.presenter

import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem

interface StepTwoPresenter {
    fun onPillSelected(pillItem: SearchMedicineItem)  // 약 이름 선택 처리
    fun handleTextChange(input: String)   // EditText의 텍스트 변경 처리
}