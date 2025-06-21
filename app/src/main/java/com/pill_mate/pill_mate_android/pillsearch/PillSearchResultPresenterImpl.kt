package com.pill_mate.pill_mate_android.pillsearch

class PillSearchResultPresenterImpl(
    private val view: PillSearchResultView
) : PillSearchResultPresenter {
    override fun selectPill(pillName: String) {
        view.onPillSelected(pillName)
    }
}