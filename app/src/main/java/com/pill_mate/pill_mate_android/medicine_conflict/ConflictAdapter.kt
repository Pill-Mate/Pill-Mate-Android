package com.pill_mate.pill_mate_android.medicine_conflict

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.databinding.ItemConflictBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse

class ConflictAdapter(
    private val onInquiryClicked: (itemSeq: String) -> Unit,
    private val onDeleteClicked: (itemSeq: String) -> Unit
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

                    binding.btnDelete.setOnClickListener {
                        item.ITEM_SEQ?.let { seq ->
                            onDeleteClicked(seq)
                        }
                    }

                    binding.btnInquiry.setOnClickListener {
                        item.ITEM_SEQ?.let { it1 -> onInquiryClicked(it1) }
                    }
                }
                is EfcyDplctResponse -> {
                    binding.tvClassName.text = item.CLASS_NAME
                    binding.tvPillName.text = item.ITEM_NAME
                    binding.tvCompanyName.text = item.ENTP_NAME
                    binding.tvWarningDetail.text = item.EFFECT_NAME

                    binding.btnDelete.setOnClickListener {
                        item.ITEM_SEQ?.let { seq ->
                            onDeleteClicked(seq)
                        }
                    }

                    binding.btnInquiry.setOnClickListener {
                        item.ITEM_SEQ?.let { it1 -> onInquiryClicked(it1) }
                    }
                }
                else -> throw IllegalArgumentException("Unsupported item type")
            }
        }

        fun disableDeleteButton() {
            binding.btnDelete.isEnabled = false
            binding.btnDelete.alpha = 0.5f
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