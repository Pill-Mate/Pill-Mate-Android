package com.pill_mate.pill_mate_android.search.presenter

interface StepOnePresenter {
    interface View {
        fun updateButtonState(isEnabled: Boolean)
        fun showWarning(isVisible: Boolean)
    }

    interface Presenter {
        fun onPharmacyNameChanged(pharmacyText: String, hospitalText: String)
        fun onNextButtonClicked(pharmacyName: String)
    }
}