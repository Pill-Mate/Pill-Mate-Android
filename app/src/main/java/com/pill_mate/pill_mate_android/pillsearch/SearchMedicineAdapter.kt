package com.pill_mate.pill_mate_android.pillsearch

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
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem

class SearchMedicineAdapter(
    private val onItemClick: (SearchMedicineItem) -> Unit = {},
    private var query: String = ""
) : RecyclerView.Adapter<SearchMedicineAdapter.MedicineViewHolder>() {

    private val medicineList = mutableListOf<SearchMedicineItem>()

    class MedicineViewHolder(
        var binding: SearchPillItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchMedicineItem, query: String) = with(binding) {
            // Glide 이미지 로딩
            if (ivImage.tag != item.itemImage) {
                ivImage.tag = item.itemImage
                Glide.with(ivImage.context).clear(ivImage)
                Glide.with(ivImage.context)
                    .load(item.itemImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .transform(RoundedCorners(8))
                    .error(R.drawable.img_default)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("GlideError", "Image Load Failed: ${item.itemImage}", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("GlideSuccess", "Image Loaded: ${item.itemImage}")
                            return false
                        }
                    })
                    .into(ivImage)
            }

            tvClassName.text = item.className
            tvCompanyName.text = item.companyName ?: ""

            // 약물명 길이 제한
            val pillName = if (item.itemName.length > 17) {
                "${item.itemName.substring(0, 17)}.."
            } else {
                item.itemName
            }

            // 검색어 하이라이트
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchPillItemBinding.inflate(layoutInflater, parent, false)
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val item = medicineList[position]
        holder.bind(item, query)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = medicineList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newList: List<SearchMedicineItem>, newQuery: String) {
        Log.d("SearchMedicineAdapter", "updateItems: ${newList.size} items")
        medicineList.clear()
        medicineList.addAll(newList)
        query = newQuery
        notifyDataSetChanged()
    }
}