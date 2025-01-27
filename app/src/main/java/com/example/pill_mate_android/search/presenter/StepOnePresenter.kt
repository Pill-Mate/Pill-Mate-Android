package com.example.pill_mate_android.search.presenter

interface StepOnePresenter {
    interface View {
        fun updateButtonState(isEnabled: Boolean)
        fun showWarning(isVisible: Boolean)
    }

    interface Presenter {
        fun onPharmacyNameChanged(text: String)
        fun onNextButtonClicked(pharmacyName: String)
    }
}