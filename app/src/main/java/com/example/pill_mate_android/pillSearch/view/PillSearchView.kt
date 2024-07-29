package com.example.pill_mate_android.pillSearch.view

import com.example.pill_mate_android.pillSearch.model.Pill

interface PillSearchView {
    fun showPills(pills: List<Pill>)
}