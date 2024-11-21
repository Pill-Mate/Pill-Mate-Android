package com.example.pill_mate_android.pillSearch.view

import com.example.pill_mate_android.pillSearch.model.HospitalItem
import com.example.pill_mate_android.pillSearch.model.PharmacyItem
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem

interface PillSearchView {
    fun showPillInfo(pills: List<PillInfoItem>)
    fun showPillIdntfc(pills: List<PillIdntfcItem>)
    fun showPharmacies(pills: List<PharmacyItem>)
    fun showHospitals(pills: List<HospitalItem>)
}