package com.example.pill_mate_android.search.view

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.SearchRecentItemBinding
import com.example.pill_mate_android.databinding.SearchResultItemBinding
import com.example.pill_mate_android.search.model.Searchable

class SearchAdapter(
    private val onItemClick: (String) -> Unit = {},  // 최근 검색어 클릭 이벤트
    private val onSearchResultClick: (String, String, String) -> Unit = { _, _, _ -> }, // 검색 결과 클릭 이벤트 (name, phone, address)
    private val onDeleteClick: (String) -> Unit = {},  // 최근 검색어 삭제 이벤트
    private var recentSearches: List<String> = emptyList(),  // 최근 검색어 리스트
    private var searchResults: List<Searchable> = emptyList(),  // 병원/약국 검색 결과
    private var showRecentSearches: Boolean = true,  // 최근 검색어 표시 여부
    private var query: String = ""  // 현재 검색어
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_RECENT_SEARCH = 0
        private const val VIEW_TYPE_SEARCH_RESULT = 1
    }

    override fun getItemCount(): Int {
        return if (showRecentSearches) recentSearches.size else searchResults.size
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
            val binding = SearchResultItemBinding.inflate(
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
            val result = searchResults[position]
            holder.bind(result, query)
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
        private val binding: SearchResultItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Searchable, query: String) {
            val name = result.getName()
            val address = result.getAddress()
            val phone = result.getNumber()

            // 검색어 강조
            if (!name.isNullOrEmpty() && query.isNotEmpty()) {
                val spannableString = SpannableString(name)
                val queryIndex = name.indexOf(query, ignoreCase = true)

                if (queryIndex >= 0) {
                    spannableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(binding.root.context, R.color.main_blue_1)
                        ),
                        queryIndex,
                        queryIndex + query.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                binding.tvName.text = spannableString
            } else {
                binding.tvName.text = name
                binding.tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
            }

            binding.tvAddress.text = address ?: "주소 정보 없음"
            binding.tvNumber.text = phone ?: "전화번호 정보 없음"

            binding.root.setOnClickListener {
                // 이름, 전화번호, 주소를 전달
                onSearchResultClick(name.orEmpty(), phone.orEmpty(), address.orEmpty())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateResults(newResults: List<Searchable>, newQuery: String) {
        searchResults = newResults
        query = newQuery
        showRecentSearches = false
        Log.d("SearchAdapter", "Updated results with query: $query, items: ${newResults.size}")
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecentSearches(newSearches: List<String>) {
        recentSearches = newSearches
        showRecentSearches = true
        notifyDataSetChanged()
    }
}