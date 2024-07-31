package com.example.pill_mate_android.pillSearch.presenter

import android.util.Log
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
        Log.d("PillSearchPresenterImpl", "searchPills called with query: $query")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pillInfo = repository.getPillInfo(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 8,
                    itemName = query
                )
                val pillIdntfc = repository.getPillIdntfc(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF%2F5JaiCg%3D%3D",
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
}