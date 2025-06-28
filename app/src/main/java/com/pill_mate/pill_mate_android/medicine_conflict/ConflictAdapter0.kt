package com.pill_mate.pill_mate_android.medicine_conflict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemConflictBinding
import com.pill_mate.pill_mate_android.medicine_conflict.model.EfcyDplctResponse0
import com.pill_mate.pill_mate_android.medicine_conflict.model.UsjntTabooResponse0

class ConflictAdapter0(
    private val onInquiryClicked: (itemSeq: String) -> Unit,
    private val onDeleteClicked: (itemSeq: String) -> Unit,
    private val showDeleteButton: Boolean = true // 추가됨
) : RecyclerView.Adapter<ConflictAdapter0.ViewHolder>() {

    private var items: List<Any> = emptyList()

    fun <T : Any> submitList(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemConflictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {
            val itemSeq = when (item) {
                is UsjntTabooResponse0 -> item.ITEM_SEQ
                is EfcyDplctResponse0 -> item.ITEM_SEQ
                else -> null
            }

            val imageUrl = when (item) {
                is UsjntTabooResponse0 -> item.ITEM_IMAGE
                is EfcyDplctResponse0 -> item.ITEM_IMAGE
                else -> null
            }

            val className = when (item) {
                is UsjntTabooResponse0 -> item.CLASS_NAME
                is EfcyDplctResponse0 -> item.CLASS_NAME
                else -> ""
            }

            val pillName = when (item) {
                is UsjntTabooResponse0 -> item.MIXTURE_ITEM_NAME
                is EfcyDplctResponse0 -> item.ITEM_NAME
                else -> ""
            }

            val entpName = when (item) {
                is UsjntTabooResponse0 -> item.ENTP_NAME
                is EfcyDplctResponse0 -> item.ENTP_NAME
                else -> ""
            }

            val warning = when (item) {
                is UsjntTabooResponse0 -> item.PROHBT_CONTENT
                is EfcyDplctResponse0 -> item.EFFECT_NAME
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

            // 버튼 설정
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