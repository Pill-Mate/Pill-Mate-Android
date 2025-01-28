package com.example.pill_mate_android.medicine_conflict

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.databinding.ItemConflictBinding
import com.example.pill_mate_android.medicine_registration.model.EfcyDplctResponse
import com.example.pill_mate_android.medicine_registration.model.UsjntTabooResponse

class ConflictAdapter(
    private val onInquiryClicked: (medicineName: String) -> Unit
) : RecyclerView.Adapter<ConflictAdapter.ViewHolder>() {

    private var items: List<Any> = emptyList()

    fun <T : Any> submitList(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemConflictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {
            when (item) {
                is UsjntTabooResponse -> {
                    binding.tvClassName.text = item.CLASS_NAME
                    binding.tvPillName.text = item.MIXTURE_ITEM_NAME
                    binding.tvCompanyName.text = item.ENTP_NAME
                    binding.tvWarningDetail.text = item.PROHBT_CONTENT

                    binding.btnInquiry.setOnClickListener {
                        item.MIXTURE_ITEM_NAME?.let { it1 -> onInquiryClicked(it1) }
                    }
                }
                is EfcyDplctResponse -> {
                    binding.tvClassName.text = item.CLASS_NAME
                    binding.tvPillName.text = item.ITEM_NAME
                    binding.tvCompanyName.text = item.ENTP_NAME
                    binding.tvWarningDetail.text = item.EFFECT_NAME

                    binding.btnInquiry.setOnClickListener {
                        item.ITEM_NAME?.let { it1 -> onInquiryClicked(it1) }
                    }
                }
                else -> throw IllegalArgumentException("Unsupported item type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConflictBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}