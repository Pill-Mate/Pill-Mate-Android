package com.example.pill_mate_android.search.view

import com.example.pill_mate_android.search.model.PillIdntfcItem
import com.example.pill_mate_android.search.model.PillInfoItem
import com.example.pill_mate_android.search.model.SearchType
import com.example.pill_mate_android.search.model.Searchable

interface PillSearchView {
    fun showPillInfo(pills: List<PillInfoItem>)
    fun showPillIdntfc(pills: List<PillIdntfcItem>)
    fun showResults(results: List<Searchable>, type: SearchType)
}