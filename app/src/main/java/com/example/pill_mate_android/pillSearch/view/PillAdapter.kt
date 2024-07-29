package com.example.pill_mate_android.pillSearch.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pill_mate_android.databinding.SuggestionRecyclerviewItemBinding
import com.example.pill_mate_android.pillSearch.model.Pill

class PillAdapter(
    private val onItemClick: (Pill) -> Unit = {}
) : RecyclerView.Adapter<PillAdapter.PillViewHolder>() {

    private val pillList = mutableListOf<Pill>()

    class PillViewHolder(
        var binding: SuggestionRecyclerviewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pill: Pill) = with(binding) {
            ivImage.load(pill.itemImage)
            tvPillName.text = pill.itemName
            tvCompanyName.text = pill.entpName
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
    fun updateItems(newPillList: List<Pill>) {
        Log.d("PillAdapter", "updateItems called with ${newPillList.size} items")
        pillList.clear()
        pillList.addAll(newPillList)
        notifyDataSetChanged()
    }
}