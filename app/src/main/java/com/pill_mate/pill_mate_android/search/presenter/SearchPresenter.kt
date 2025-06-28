package com.pill_mate.pill_mate_android.search.presenter

import com.pill_mate.pill_mate_android.search.model.SearchType

interface SearchPresenter {
    fun search(query: String, type: SearchType)
    fun searchMedicines(query: String)
}