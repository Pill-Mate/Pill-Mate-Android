package com.example.pill_mate_android.util

import android.content.Context
import android.content.SharedPreferences
import com.example.pill_mate_android.search.model.SearchType
import org.json.JSONArray

class SharedPreferencesHelper(context: Context, private val searchType: SearchType) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    private val key: String
        get() = if (searchType == SearchType.HOSPITAL) "recent_hospital_searches" else "recent_pharmacy_searches"

    fun getRecentSearches(): List<String> {
        val jsonString = prefs.getString(key, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        return List(jsonArray.length()) { jsonArray.getString(it) }
    }

    fun saveSearchTerm(term: String) {
        val searches = getRecentSearches().toMutableList()
        if (!searches.contains(term)) {
            searches.add(0, term)
            if (searches.size > 10) searches.removeAt(10)
            saveSearchesToPrefs(searches)
        }
    }

    fun deleteSearchTerm(term: String) {
        val searches = getRecentSearches().toMutableList()
        searches.remove(term)
        saveSearchesToPrefs(searches)
    }

    fun clearAllSearches() {
        prefs.edit().remove(key).apply()
    }

    private fun saveSearchesToPrefs(searches: List<String>) {
        val jsonArray = JSONArray(searches)
        prefs.edit().putString(key, jsonArray.toString()).apply()
    }
}