package com.example.pill_mate_android.pillSearch.presenter

import android.util.Log
import com.example.pill_mate_android.pillSearch.model.DataRepository
import com.example.pill_mate_android.pillSearch.model.Medicine
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.view.StepTwoView

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

        val savedMedicine = DataRepository.getMedicine()
        Log.d("StepTwoPresenterImpl", "Saved Medicine: $savedMedicine")
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
            storage = ""
        )
    }
}