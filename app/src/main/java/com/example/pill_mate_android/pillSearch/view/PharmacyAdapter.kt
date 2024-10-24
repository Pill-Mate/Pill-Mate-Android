package com.example.pill_mate_android.pillSearch.view

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.SearchRecentItemBinding
import com.example.pill_mate_android.databinding.SearchPharmacyItemBinding
import com.example.pill_mate_android.pillSearch.model.PharmacyItem

class PharmacyAdapter(
    private val onItemClick: (String) -> Unit = {},
    private val onSearchResultClick: (String) -> Unit = {},
    private val onDeleteClick: (String) -> Unit = {},
    private var recentSearches: List<String> = emptyList(),
    private var pharmacyList: List<PharmacyItem> = emptyList(),
    private var showRecentSearches: Boolean = true,
    private var query: String = "" // 현재 검색어 저장
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_RECENT_SEARCH = 0
        private const val VIEW_TYPE_SEARCH_RESULT = 1
    }

    override fun getItemCount(): Int {
        return if (showRecentSearches) recentSearches.size else pharmacyList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (showRecentSearches) VIEW_TYPE_RECENT_SEARCH else VIEW_TYPE_SEARCH_RESULT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RECENT_SEARCH) {
            val binding = SearchRecentItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            RecentSearchViewHolder(binding)
        } else {
            val binding = SearchPharmacyItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            SearchResultViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecentSearchViewHolder) {
            val recentSearch = recentSearches[position]
            holder.bind(recentSearch)
        } else if (holder is SearchResultViewHolder) {
            val pharmacy = pharmacyList[position]
            holder.bind(pharmacy, query) // query 전달
        }
    }

    inner class RecentSearchViewHolder(
        private val binding: SearchRecentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(searchTerm: String) {
            binding.tvSearchTerm.text = searchTerm
            binding.root.setOnClickListener { onItemClick(searchTerm) }
            binding.ivDelete.setOnClickListener { onDeleteClick(searchTerm) }
        }
    }

    inner class SearchResultViewHolder(
        private val binding: SearchPharmacyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pharmacy: PharmacyItem, query: String) {
            val spannableString = SpannableString(pharmacy.dutyName)
            val queryIndex = pharmacy.dutyName.indexOf(query, ignoreCase = true)
            if (queryIndex >= 0) {
                spannableString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(binding.root.context, R.color.main_blue_1)
                    ),
                    queryIndex, queryIndex + query.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            binding.tvPharmacyName.text = spannableString
            binding.tvPharmacyAddress.text = pharmacy.dutyAddr
            binding.tvPharmacyNumber.text = pharmacy.dutyTel1

            binding.root.setOnClickListener {
                onSearchResultClick(pharmacy.dutyName)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePharmacies(newPharmacyList: List<PharmacyItem>, newQuery: String) {
        pharmacyList = newPharmacyList
        query = newQuery // 검색어 업데이트
        showRecentSearches = false
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecentSearches(newSearches: List<String>) {
        recentSearches = newSearches
        showRecentSearches = true
        notifyDataSetChanged()
    }
}