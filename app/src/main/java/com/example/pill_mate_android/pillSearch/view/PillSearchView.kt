package com.example.pill_mate_android.pillSearch.view

import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem

interface PillSearchView {
    fun showPillInfo(pills: List<PillInfoItem>)
    fun showPillIdntfc(pills: List<PillIdntfcItem>)
}