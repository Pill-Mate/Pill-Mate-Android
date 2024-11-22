package com.example.pill_mate_android.pillSearch.presenter

import com.example.pill_mate_android.pillSearch.view.StepTwoView

class StepTwoPresenterImpl(
    private val stepTwoView: StepTwoView
) : StepTwoPresenter {

    override fun onPillSelected(pillName: String) {
        stepTwoView.updatePillName(pillName)  // 약 이름 업데이트
    }

    override fun handleTextChange(input: String) {
        val isEnabled = input.isNotEmpty()
        stepTwoView.updateButtonState(isEnabled)
    }
}