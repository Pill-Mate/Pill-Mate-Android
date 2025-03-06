package com.pill_mate.pill_mate_android.search.presenter

class StepOnePresenterImpl(
    private val view: StepOnePresenter.View
) : StepOnePresenter.Presenter {

    override fun onPharmacyNameChanged(text: String) {
        view.updateButtonState(text.isNotEmpty())
        if (text.isNotEmpty()) {
            view.showWarning(false)
        }
    }

    override fun onNextButtonClicked(pharmacyName: String) {
        if (pharmacyName.isEmpty()) {
            view.showWarning(true)
        }
    }
}