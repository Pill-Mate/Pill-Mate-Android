package com.pill_mate.pill_mate_android.search.presenter

import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.Medicine
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import com.pill_mate.pill_mate_android.search.view.StepTwoView

class StepTwoPresenterImpl(
    private val stepTwoView: StepTwoView
) : StepTwoPresenter {

    override fun onPillSelected(pillItem: SearchMedicineItem) {
        // StepTwoView에 선택된 약물 이름 업데이트
        stepTwoView.updatePillName(pillItem.itemName)

        // PillIdntfcItem -> Medicine 변환
        val medicine = convertToMedicine(pillItem)

        // DataRepository에 저장
        DataRepository.saveMedicine(medicine)
    }

    override fun handleTextChange(input: String) {
        val isEnabled = input.isNotEmpty()
        stepTwoView.updateButtonState(isEnabled)
    }

    private fun convertToMedicine(pillItem: SearchMedicineItem): Medicine {
        return Medicine(
            identify_number = pillItem.itemSeq.toString(),
            medicine_name = pillItem.itemName,
            ingredient = "",
            image = pillItem.itemImage,
            classname = pillItem.className,
            efficacy = "",
            side_effect = "",
            caution = "",
            storage = "",
            entp_name = pillItem.entpName
        )
    }
}