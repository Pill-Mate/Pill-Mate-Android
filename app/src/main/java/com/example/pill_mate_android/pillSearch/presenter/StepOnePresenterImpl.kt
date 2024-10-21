package com.example.pill_mate_android.pillSearch.presenter

class StepOnePresenterImpl(
    private val view: StepOnePresenter.View
) : StepOnePresenter.Presenter {

    override fun onPharmacyNameChanged(text: String) {
        view.updateButtonState(text.isNotEmpty())
    }

    override fun onNextButtonClicked(pharmacyName: String) {
        if (pharmacyName.isEmpty()) {
            view.showWarning(true)
        } else {
            view.showWarning(false)
        }
    }
}
