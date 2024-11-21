package com.example.pill_mate_android.pillSearch.presenter

import android.util.Log
import com.example.pill_mate_android.pillSearch.model.HospitalItem
import com.example.pill_mate_android.pillSearch.model.PharmacyItem
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import com.example.pill_mate_android.pillSearch.model.PillInfoItem
import com.example.pill_mate_android.pillSearch.model.PillRepository
import com.example.pill_mate_android.pillSearch.view.PillSearchView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PillSearchPresenterImpl(
    private val view: PillSearchView,
    private val repository: PillRepository = PillRepository()
) : PillSearchPresenter {

    override fun searchPills(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pillInfo = repository.getPillInfo(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 8,
                    itemName = query
                )
                val pillIdntfc = repository.getPillIdntfc(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 8,
                    item_name = query
                )

                Log.d("PillSearchPresenterImpl", "API response: $pillIdntfc")
                withContext(Dispatchers.Main) {
                    if (pillIdntfc != null) {
                        val filteredPills = filterPillIdntfc(pillIdntfc, query)
                        Log.d("PillSearchPresenterImpl", "Filtered pills: $filteredPills")
                        view.showPillIdntfc(filteredPills)
                    } else {
                        view.showPillInfo(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("PillSearchPresenterImpl", "Error fetching pills", e)
                withContext(Dispatchers.Main) {
                    view.showPillInfo(emptyList())
                }
            }
        }
    }

    override fun searchPharmacies(query: String) {
        Log.d("PillSearchPresenterImpl", "searchPharmacies called with query: $query")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pharmacyList = repository.getPharmacyList(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 10,
                    order = "name",
                    name = query,
                )

                withContext(Dispatchers.Main) {
                    if (pharmacyList!= null) {
                        val filteredPharmacies = filterPharmacyList(pharmacyList, query)
                        Log.d("pharmacyListSearchPresenterImpl", "Filtered pharmacies: $filteredPharmacies")
                        view.showPharmacies(filteredPharmacies)
                    } else {
                        view.showPharmacies(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("pharmacyListSearchPresenterImpl", "Error fetching pharmacies", e)
                withContext(Dispatchers.Main) {
                    view.showPharmacies(emptyList())
                }
            }
        }
    }

    override fun searchHospitals(query: String) {
        Log.d("PillSearchPresenterImpl", "searchHospitals called with query: $query")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hospitalList = repository.getHospitalList(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 10,
                    order = "name",
                    name = query
                )

                withContext(Dispatchers.Main) {
                    if (hospitalList != null) {
                        val filteredHospitals = filterHospitalList(hospitalList, query)
                        Log.d("PillSearchPresenterImpl", "Filtered hospitals: $filteredHospitals")
                        view.showHospitals(filteredHospitals)
                    } else {
                        view.showHospitals(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("PillSearchPresenterImpl", "Error fetching hospitals", e)
                withContext(Dispatchers.Main) {
                    view.showHospitals(emptyList())
                }
            }
        }
    }

    private fun filterPillInfo(pills: List<PillInfoItem>, query: String): List<PillInfoItem> {
        val startsWithQuery = pills.filter { it.itemName?.startsWith(query, ignoreCase = true) == true }
        val containsQuery = pills.filter { it.itemName?.contains(query, ignoreCase = true) == true && it !in startsWithQuery }
        return (startsWithQuery + containsQuery).take(20)
    }

    private fun filterPillIdntfc(pills: List<PillIdntfcItem>, query: String): List<PillIdntfcItem> {
        val startsWithQuery = pills.filter { it.ITEM_NAME?.startsWith(query, ignoreCase = true) == true }
        val containsQuery = pills.filter { it.ITEM_NAME?.contains(query, ignoreCase = true) == true && it !in startsWithQuery }
        return (startsWithQuery + containsQuery).take(20)
    }

    private fun filterPharmacyList(pharmacies: List<PharmacyItem>, query: String): List<PharmacyItem> {
        val startsWithQuery = pharmacies.filter { it.dutyName?.startsWith(query, ignoreCase = true) == true }
        val containsQuery = pharmacies.filter { it.dutyName?.contains(query, ignoreCase = true) == true && it !in startsWithQuery }
        return (startsWithQuery + containsQuery).take(20)
    }

    private fun filterHospitalList(
        hospitals: List<HospitalItem>, query: String
    ): List<HospitalItem> {
        val startsWithQuery = hospitals.filter {
            it.dutyName?.startsWith(query, ignoreCase = true) == true
        }
        val containsQuery = hospitals.filter {
            it.dutyName?.contains(query, ignoreCase = true) == true && it !in startsWithQuery
        }
        return (startsWithQuery + containsQuery).take(20)
    }
}