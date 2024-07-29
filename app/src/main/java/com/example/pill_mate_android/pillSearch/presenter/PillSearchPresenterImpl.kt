package com.example.pill_mate_android.pillSearch.presenter

import android.util.Log
import com.example.pill_mate_android.pillSearch.model.Pill
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
                val pills = repository.getPills(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==", // 나중에 키 숨겨야함
                    pageNo = 1,
                    numOfRows = 10,
                    itemName = query
                )
                Log.d("PillSearchPresenterImpl", "API response: $pills")
                withContext(Dispatchers.Main) {
                    if (pills != null) {
                        val filteredPills = filterPills(pills, query)
                        view.showPills(filteredPills)
                    } else {
                        view.showPills(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("PillSearchPresenterImpl", "Error fetching pills", e)
                withContext(Dispatchers.Main) {
                    view.showPills(emptyList())
                }
            }
        }
    }

    private fun filterPills(pills: List<Pill>, query: String): List<Pill> {
        val startsWithQuery = pills.filter { it.itemName?.startsWith(query, ignoreCase = true) == true }
        val containsQuery = pills.filter { it.itemName?.contains(query, ignoreCase = true) == true && it !in startsWithQuery }
        return (startsWithQuery + containsQuery).take(20)
    }
}