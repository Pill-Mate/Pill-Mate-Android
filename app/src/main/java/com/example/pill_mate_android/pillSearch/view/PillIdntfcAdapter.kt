package com.example.pill_mate_android.pillSearch.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pill_mate_android.databinding.SuggestionRecyclerviewItemBinding
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem

class PillIdntfcAdapter(
    private val onItemClick: (PillIdntfcItem) -> Unit = {}
) : RecyclerView.Adapter<PillIdntfcAdapter.PillViewHolder>() {

    private val pillList = mutableListOf<PillIdntfcItem>()

    class PillViewHolder(
        var binding: SuggestionRecyclerviewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pill: PillIdntfcItem) = with(binding) {
            ivImage.load(pill.ITEM_IMAGE)
            tvClassName.text = pill.CLASS_NAME
            tvPillName.text = pill.ITEM_NAME
            tvCompanyName.text = pill.ENTP_NAME
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SuggestionRecyclerviewItemBinding.inflate(layoutInflater, parent, false)
        return PillViewHolder(binding)
    }

    override fun getItemCount(): Int = pillList.size

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        val pill = pillList[position]
        holder.bind(pill)
        holder.binding.root.setOnClickListener {
            onItemClick(pill)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newPillList: List<PillIdntfcItem>) {
        Log.d("PillAdapter", "updateItems called with ${newPillList.size} items")
        pillList.clear()
        pillList.addAll(newPillList)
        notifyDataSetChanged()
    }
}