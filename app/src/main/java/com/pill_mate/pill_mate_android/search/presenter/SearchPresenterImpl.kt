package com.pill_mate.pill_mate_android.search.presenter

import android.util.Log
import com.pill_mate.pill_mate_android.BuildConfig
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem
import com.pill_mate.pill_mate_android.search.model.PillRepository
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import com.pill_mate.pill_mate_android.search.model.Searchable
import com.pill_mate.pill_mate_android.search.model.SearchType
import com.pill_mate.pill_mate_android.search.view.PillSearchView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchPresenterImpl(
    private val view: PillSearchView,
    private val repository: PillRepository = PillRepository()
) : SearchPresenter {

    override fun search(query: String, type: SearchType) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val results = repository.getSearchResults(
                    name = query,
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

    override fun searchMedicines(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val medicines = repository.getSearchMedicineResults(query)
                val filteredMedicines = filterMedicineResults(medicines, query)

                withContext(Dispatchers.Main) {
                    view.showMedicines(filteredMedicines)
                }
            } catch (e: Exception) {
                Log.e("PillSearchPresenterImpl", "Error fetching pills", e)
            }
        }
    }

    private fun <T : Searchable> filterResults(items: List<T>?, query: String): List<T> {
        if (items.isNullOrEmpty()) return emptyList()

        val queryLower = query.lowercase()
        val (startsWith, remaining) = items.partition {
            it.getName()?.lowercase()?.startsWith(queryLower) == true
        }
        val contains = remaining.filter {
            it.getName()?.lowercase()?.contains(queryLower) == true
        }
        return (startsWith + contains).take(20)
    }

    private fun filterMedicineResults(items: List<SearchMedicineItem>, query: String): List<SearchMedicineItem> {
        val queryLower = query.lowercase()
        val (startsWith, remaining) = items.partition {
            it.itemName.lowercase().startsWith(queryLower)
        }
        val contains = remaining.filter {
            it.itemName.lowercase().contains(queryLower)
        }
        return (startsWith + contains).take(20)
    }
}