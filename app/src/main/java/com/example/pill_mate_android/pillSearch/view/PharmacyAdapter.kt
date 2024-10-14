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
import com.example.pill_mate_android.databinding.SearchPharmacyItemBinding
import com.example.pill_mate_android.pillSearch.model.PharmacyItem

class PharmacyAdapter(
    private val onItemClick: (PharmacyItem) -> Unit = {},
    private var query: String = ""
) : RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder>() {

    private val pharmacyList = mutableListOf<PharmacyItem>()

    class PharmacyViewHolder(
        var binding: SearchPharmacyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pharmacy: PharmacyItem, query: String) = with(binding) {
            tvPharmacyName.text = pharmacy.dutyName
            tvPharmacyAddress.text = pharmacy.dutyAddr
            tvPharmacyNumber.text = pharmacy.dutyTel1

            val pharmacyName = pharmacy.dutyName
            val spannableString = SpannableString(pharmacyName)

            // 검색어가 포함된 경우 색상 변경
            val queryIndex = pharmacyName.indexOf(query, ignoreCase = true)
            if (queryIndex >= 0) {
                val blueColor = ContextCompat.getColor(binding.root.context, R.color.main_blue_1)
                spannableString.setSpan(
                    ForegroundColorSpan(blueColor),
                    queryIndex,
                    queryIndex + query.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                // 검색어가 포함되지 않은 경우 기본 색상으로 설정
                tvPharmacyName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
            }

            // 최종적으로 SpannableString을 TextView에 설정
            tvPharmacyName.text = spannableString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyViewHolder {
        val binding = SearchPharmacyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PharmacyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PharmacyViewHolder, position: Int) {
        val pharmacy = pharmacyList[position]
        holder.bind(pharmacy, query)
        holder.binding.root.setOnClickListener {
            onItemClick(pharmacy)
        }
    }

    override fun getItemCount(): Int = pharmacyList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newPharmacyList: List<PharmacyItem>, newQuery: String) {
        pharmacyList.clear()
        pharmacyList.addAll(newPharmacyList)
        query = newQuery
        notifyDataSetChanged()
    }
}