package com.pill_mate.pill_mate_android.medicine_conflict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemConflictBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse

class ConflictAdapter(
    private val onInquiryClicked: (itemSeq: String) -> Unit,
    private val onDeleteClicked: (itemSeq: String) -> Unit,
    private val showDeleteButton: Boolean = true
) : RecyclerView.Adapter<ConflictAdapter.ViewHolder>() {

    private var items: List<Any> = emptyList()

    fun <T : Any> submitList(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemConflictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {
            val itemSeq = when (item) {
                is UsjntTabooResponse -> item.mixtureItemSeq
                is EfcyDplctResponse -> item.itemSeq
                else -> null
            }

            // 이미지 필드 반영
            val imageUrl = when (item) {
                is UsjntTabooResponse -> item.item_image
                is EfcyDplctResponse -> item.item_image
                else -> null
            }

            val className = when (item) {
                is UsjntTabooResponse -> item.className
                is EfcyDplctResponse -> item.className
                else -> ""
            }

            val pillName = when (item) {
                is UsjntTabooResponse -> item.mixItemName
                is EfcyDplctResponse -> item.itemName
                else -> ""
            }

            val entpName = when (item) {
                is UsjntTabooResponse -> item.entpName
                is EfcyDplctResponse -> item.entpName
                else -> ""
            }

            val warning = when (item) {
                is UsjntTabooResponse -> item.prohbtContent
                is EfcyDplctResponse -> item.effectName
                else -> ""
            }

            binding.tvClassName.text = className
            binding.tvPillName.text = pillName
            binding.tvCompanyName.text = entpName
            binding.tvWarningDetail.text = warning

            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.ivImage.context)
                    .load(imageUrl)
                    .transform(RoundedCorners(8))
                    .error(R.drawable.img_default)
                    .into(binding.ivImage)
            } else {
                binding.ivImage.setImageResource(R.drawable.img_default)
            }

            binding.btnInquiry.setOnClickListener {
                itemSeq?.let { onInquiryClicked(it) }
            }

            if (showDeleteButton) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener {
                    itemSeq?.let { onDeleteClicked(it) }
                }
            } else {
                binding.btnDelete.visibility = View.GONE
            }
        }

        fun disableDeleteButton() {
            binding.btnDelete.isEnabled = false
            binding.btnDelete.alpha = 0.5f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConflictBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}