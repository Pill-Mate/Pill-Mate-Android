package com.pill_mate.pill_mate_android.search.presenter

class StepOnePresenterImpl(
    private val view: StepOnePresenter.View
) : StepOnePresenter.Presenter {

    override fun onPharmacyNameChanged(pharmacyText: String, hospitalText: String) {
        val isPharmacyEmpty = pharmacyText.isEmpty()
        val isHospitalNotEmpty = hospitalText.isNotEmpty()

        view.updateButtonState(!isPharmacyEmpty)

        if (isPharmacyEmpty && isHospitalNotEmpty) {
            view.showWarning(true)  // 병원만 입력된 경우 경고 표시
        } else {
            view.showWarning(false) // 나머지 경우 경고 숨김
        }
    }

    override fun onNextButtonClicked(pharmacyName: String) {
        if (pharmacyName.isEmpty()) {
            view.showWarning(true)
        }
    }
}