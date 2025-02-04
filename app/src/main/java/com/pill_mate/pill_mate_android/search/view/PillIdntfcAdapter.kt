package com.pill_mate.pill_mate_android.search.view

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import androidx.core.content.ContextCompat
import coil.ImageLoader
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.SearchPillItemBinding
import com.pill_mate.pill_mate_android.search.model.PillIdntfcItem

class PillIdntfcAdapter(
    private val imageLoader: ImageLoader, // ✅ 수정됨: ImageLoader를 외부에서 주입받도록 변경
    private val onItemClick: (PillIdntfcItem) -> Unit = {}, // 클릭 리스너
    private var query: String = "" // 검색어 저장
) : RecyclerView.Adapter<PillIdntfcAdapter.PillViewHolder>() {

    private val pillList = mutableListOf<PillIdntfcItem>()

    class PillViewHolder(
        var binding: SearchPillItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pill: PillIdntfcItem, query: String, imageLoader: ImageLoader) = with(binding) {
            if (ivImage.tag != pill.ITEM_IMAGE) { // ✅ 수정됨: 중복 로딩 방지
                ivImage.tag = pill.ITEM_IMAGE

                ivImage.load(pill.ITEM_IMAGE, imageLoader) {
                    diskCachePolicy(CachePolicy.ENABLED)
                    memoryCachePolicy(CachePolicy.ENABLED)
                    crossfade(false)
                    size(100, 54) // ✅ 수정됨: 크기 최적화
                    transformations(RoundedCornersTransformation(8f))
                    //error(R.drawable.error_placeholder) // ✅ 오류 시 기본 이미지 추가
                }
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
        holder.bind(pill, query, imageLoader)
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