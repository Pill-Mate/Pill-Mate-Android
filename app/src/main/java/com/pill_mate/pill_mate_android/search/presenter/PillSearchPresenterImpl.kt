package com.pill_mate.pill_mate_android.search.presenter

import android.util.Log
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.search.model.PillRepository
import com.pill_mate.pill_mate_android.search.model.Searchable
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.view.PillSearchView
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

                val pillIdntfc = repository.getPillIdntfc(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 10,
                    item_name = query
                )

                Log.d("PillSearchPresenterImpl", "API response: $pillIdntfc")
                withContext(Dispatchers.Main) {
                    if (pillIdntfc != null) {
                        val filteredPills = filterPillIdntfc(pillIdntfc, query)
                        Log.d("PillSearchPresenterImpl", "Filtered pills: $filteredPills")
                        view.showPillIdntfc(filteredPills)
                    }
                }
            } catch (e: Exception) {
                Log.e("PillSearchPresenterImpl", "Error fetching pills", e)
            }
        }
    }

    override fun search(query: String, type: SearchType) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val results = repository.getSearchResults(
                    serviceKey = "g1IkFj8ICy3zimNJ5VsaEE4Wf24rGJeWKMy89pvDyZyHcuGqUHwqVv8UBxvCkCAdRJx3OpCe8yuG9tF/5JaiCg==",
                    pageNo = 1,
                    numOfRows = 10,
                    name = query,
                    order = "name",
                    type = type
                )

                val filteredResults = filterResults(results, query)

                withContext(Dispatchers.Main) {
                    view.showResults(filteredResults, type)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.showResults(emptyList(), type)
                }
            }
        }
    }

    private fun filterPillIdntfc(pills: List<PillIdntfcItem>, query: String): List<PillIdntfcItem> {
        val startsWithQuery = pills.filter { it.ITEM_NAME?.startsWith(query, ignoreCase = true) == true }
        val containsQuery = pills.filter { it.ITEM_NAME?.contains(query, ignoreCase = true) == true && it !in startsWithQuery }
        return (startsWithQuery + containsQuery).take(20)
    }

    private fun <T : Searchable> filterResults(items: List<T>?, query: String): List<T> {
        if (items.isNullOrEmpty()) return emptyList()

        val startsWithQuery = items.filter { it.getName()?.startsWith(query, ignoreCase = true) == true }
        val containsQuery = items.filter { it.getName()?.contains(query, ignoreCase = true) == true && it !in startsWithQuery }
        return (startsWithQuery + containsQuery).take(20)
    }
}