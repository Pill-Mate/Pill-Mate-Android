package com.example.pill_mate_android.pillSearch.view

import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.model.SearchType
import com.example.pill_mate_android.pillSearch.model.Searchable

interface PillSearchView {
    fun showPillInfo(pills: List<PillInfoItem>)
    fun showPillIdntfc(pills: List<PillIdntfcItem>)
    fun showResults(results: List<Searchable>, type: SearchType)
}