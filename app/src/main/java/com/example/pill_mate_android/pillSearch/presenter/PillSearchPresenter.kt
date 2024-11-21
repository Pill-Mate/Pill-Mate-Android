package com.example.pill_mate_android.pillSearch.presenter

import com.example.pill_mate_android.pillSearch.model.SearchType

interface PillSearchPresenter {
    fun searchPills(query: String)
    fun search(query: String, type: SearchType)
}