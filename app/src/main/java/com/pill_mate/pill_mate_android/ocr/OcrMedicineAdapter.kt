package com.pill_mate.pill_mate_android.ocr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.databinding.ItemOcrMedicineBinding
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem

class OcrMedicineAdapter(
    private val onClick: (SearchMedicineItem) -> Unit
) : RecyclerView.Adapter<OcrMedicineAdapter.ViewHolder>() {

    private val items = mutableListOf<SearchMedicineItem>()

    fun submit(newItems: List<SearchMedicineItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOcrMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemOcrMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchMedicineItem) {
            binding.tvName.text = item.itemName
            binding.tvSub.text = listOf(item.entpName, item.className)
                .filter { it.isNotBlank() }
                .joinToString(" · ")
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}