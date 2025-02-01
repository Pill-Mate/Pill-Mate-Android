package com.pill_mate.pill_mate_android.search.presenter

import com.pill_mate.pill_mate_android.search.model.SearchType

interface PillSearchPresenter {
    fun searchPills(query: String)
    fun search(query: String, type: SearchType)
}