package com.pill_mate.pill_mate_android.search.view

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.SearchPillItemBinding
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem

class PillIdntfcAdapter(
    private val onItemClick: (PillIdntfcItem) -> Unit = {}, // 클릭 리스너
    private var query: String = "" // 검색어 저장
) : RecyclerView.Adapter<PillIdntfcAdapter.PillViewHolder>() {

    private val pillList = mutableListOf<PillIdntfcItem>()

    class PillViewHolder(
        var binding: SearchPillItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pill: PillIdntfcItem, query: String) = with(binding) {
            if (ivImage.tag != pill.ITEM_IMAGE) { // 중복 로딩 방지
                ivImage.tag = pill.ITEM_IMAGE

                Glide.with(ivImage.context).clear(ivImage) // 기존 이미지 제거 후 로드
                Glide.with(ivImage.context)
                    .load(pill.ITEM_IMAGE)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .transform(RoundedCorners(8))
                    .error(R.drawable.img_default) // Glide 로딩 실패 시 기본 이미지
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("GlideError", "Image Load Failed for URL: ${pill.ITEM_IMAGE}", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("GlideSuccess", "Image Loaded Successfully: ${pill.ITEM_IMAGE}")
                            return false
                        }
                    })
                    .into(ivImage)
            }

            tvClassName.text = pill.CLASS_NAME
            tvCompanyName.text = pill.ENTP_NAME

            // 약물명 길이 제한 (17자 초과 시 ...)
            val pillName = if (pill.ITEM_NAME.length > 17) {
                "${pill.ITEM_NAME.substring(0, 17)}.."
            } else {
                pill.ITEM_NAME
            }

            // 검색어 하이라이트 (색상 강조)
            val spannableString = SpannableString(pillName)
            val queryIndex = pillName.indexOf(query, ignoreCase = true)

            if (queryIndex >= 0) {
                val blueColor = ContextCompat.getColor(binding.root.context, R.color.main_blue_1)
                spannableString.setSpan(
                    ForegroundColorSpan(blueColor),
                    queryIndex,
                    queryIndex + query.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                tvPillName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
            }

            tvPillName.text = spannableString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchPillItemBinding.inflate(layoutInflater, parent, false)
        return PillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        val pill = pillList[position]
        holder.bind(pill, query)
        holder.itemView.setOnClickListener { onItemClick(pill) }
    }

    override fun getItemCount(): Int = pillList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newPillList: List<PillIdntfcItem>, newQuery: String) {
        Log.d("PillAdapter", "updateItems called with ${newPillList.size} items")
        pillList.clear()
        pillList.addAll(newPillList)
        query = newQuery
        notifyDataSetChanged()
    }
}