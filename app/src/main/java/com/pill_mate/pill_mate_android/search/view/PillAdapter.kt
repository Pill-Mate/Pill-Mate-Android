package com.pill_mate.pill_mate_android.search.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pill_mate.pill_mate_android.databinding.SearchPillItemBinding
import com.pill_mate.pill_mate_android.search.model.PillInfoItem

class PillAdapter(
    private val onItemClick: (PillInfoItem) -> Unit = {}
) : RecyclerView.Adapter<PillAdapter.PillViewHolder>() {

    private val pillList = mutableListOf<PillInfoItem>()

    class PillViewHolder(
        var binding: SearchPillItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pill: PillInfoItem) = with(binding) {
            ivImage.load(pill.itemImage)
            tvPillName.text = pill.itemName
            tvCompanyName.text = pill.entpName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchPillItemBinding.inflate(layoutInflater, parent, false)
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
    fun updateItems(newPillList: List<PillInfoItem>) {
        Log.d("PillAdapter", "updateItems called with ${newPillList.size} items")
        pillList.clear()
        pillList.addAll(newPillList)
        notifyDataSetChanged()
    }
}