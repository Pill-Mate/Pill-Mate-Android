package com.pill_mate.pill_mate_android.search.view

import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.model.Searchable

interface PillSearchView {
    fun showResults(results: List<Searchable>, type: SearchType)
    fun showMedicines(pills: List<SearchMedicineItem>)
}