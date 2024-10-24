package com.example.pill_mate_android.pillSearch.util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class SharedPreferencesHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    // 검색어 저장
    fun saveSearchTerm(term: String) {
        val searches = getRecentSearches().toMutableList()
        if (!searches.contains(term)) {
            searches.add(0, term) // 최근 검색어가 상단으로
            if (searches.size > 10) searches.removeAt(10) // 최대 10개 유지
            saveSearchesToPrefs(searches) // 저장
        }
    }

    // 검색어 조회
    fun getRecentSearches(): List<String> {
        val jsonString = prefs.getString("recent_searches", null) ?: return emptyList()
        return JSONArray(jsonString).let { jsonArray ->
            List(jsonArray.length()) { jsonArray.getString(it) }
        }
    }

    // 특정 검색어 삭제
    fun deleteSearchTerm(term: String) {
        val searches = getRecentSearches().toMutableList()
        searches.remove(term)
        saveSearchesToPrefs(searches)
    }

    // 전체 검색어 삭제
    fun clearAllSearches() {
        prefs.edit().remove("recent_searches").apply()
    }

    // 검색어 리스트를 JSON 형식으로 저장
    private fun saveSearchesToPrefs(searches: List<String>) {
        val jsonArray = JSONArray().apply {
            searches.forEach { put(it) }
        }
        prefs.edit().putString("recent_searches", jsonArray.toString()).apply()
    }
}