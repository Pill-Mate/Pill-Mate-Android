package com.example.pill_mate_android.pillSearch.presenter

interface StepTwoPresenter {
    fun onPillSelected(pillName: String)  // 약 이름 선택 처리
    fun handleTextChange(input: String)   // EditText의 텍스트 변경 처리
}