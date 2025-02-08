package com.pill_mate.pill_mate_android.search.presenter

import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.Medicine
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.search.view.StepTwoView

class StepTwoPresenterImpl(
    private val stepTwoView: StepTwoView
) : StepTwoPresenter {

    override fun onPillSelected(pillItem: PillIdntfcItem) {
        // StepTwoView에 선택된 약물 이름 업데이트
        stepTwoView.updatePillName(pillItem.ITEM_NAME)

        // PillIdntfcItem -> Medicine 변환
        val medicine = convertToMedicine(pillItem)

        // DataRepository에 저장
        DataRepository.saveMedicine(medicine)
    }

    override fun handleTextChange(input: String) {
        val isEnabled = input.isNotEmpty()
        stepTwoView.updateButtonState(isEnabled)
    }

    private fun convertToMedicine(pillItem: PillIdntfcItem): Medicine {
        return Medicine(
            identify_number = pillItem.ITEM_SEQ,
            medicine_name = pillItem.ITEM_NAME,
            ingredient = pillItem.CHART,
            image = pillItem.ITEM_IMAGE,
            classname = pillItem.CLASS_NAME ?: "",
            efficacy = "",
            side_effect = "",
            caution = "",
            storage = "",
            entp_name = pillItem.ENTP_NAME
        )
    }
}