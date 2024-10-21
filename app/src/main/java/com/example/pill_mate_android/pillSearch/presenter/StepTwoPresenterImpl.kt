package com.example.pill_mate_android.pillSearch.presenter

import com.example.pill_mate_android.pillSearch.view.StepTwoView

class StepTwoPresenterImpl(
    private val stepTwoView: StepTwoView
) : StepTwoPresenter {

    override fun onPillSelected(pillName: String) {
        stepTwoView.updatePillName(pillName)  // 약 이름 업데이트
        stepTwoView.updateButtonState(true)  // 버튼 활성화
    }

    override fun handleTextChange(input: String) {
        // 입력된 텍스트가 있을 경우 버튼 활성화, 없으면 비활성화
        val isEnabled = input.isNotEmpty()
        stepTwoView.updateButtonState(isEnabled)
    }
}