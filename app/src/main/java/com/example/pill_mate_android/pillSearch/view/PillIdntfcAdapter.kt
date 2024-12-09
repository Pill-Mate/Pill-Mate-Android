package com.example.pill_mate_android.pillSearch.view

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pill_mate_android.pillSearch.model.PillIdntfcItem
import androidx.core.content.ContextCompat
import coil.transform.RoundedCornersTransformation
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.SearchPillItemBinding

class PillIdntfcAdapter(
    private val onItemClick: (PillIdntfcItem) -> Unit = {}, // 람다 표현식의 타입 명시
    private var query: String = "" // 검색어를 저장할 변수
) : RecyclerView.Adapter<PillIdntfcAdapter.PillViewHolder>() {

    private val pillList = mutableListOf<PillIdntfcItem>()

    class PillViewHolder(
        var binding: SearchPillItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pill: PillIdntfcItem, query: String) = with(binding) {
            ivImage.load(pill.ITEM_IMAGE) {
                transformations(RoundedCornersTransformation(4f))
            }
            tvClassName.text = pill.CLASS_NAME
            tvPillName.text = pill.ITEM_NAME
            tvCompanyName.text = pill.ENTP_NAME

            // 약물명 자르기 (19자 초과 시 ...)
            val pillName = if (pill.ITEM_NAME.length > 19) {
                "${pill.ITEM_NAME.substring(0, 19)}.."
            } else {
                pill.ITEM_NAME
            }

            // 검색어를 사용한 SpannableString 생성
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
        holder.bind(pill, query) // 검색어와 아이템을 bind에 전달
        holder.binding.root.setOnClickListener {
            onItemClick(pill) // 클릭 시 콜백 호출
        }
    }

    override fun getItemCount(): Int = pillList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newPillList: List<PillIdntfcItem>, newQuery: String) {
        Log.d("PillAdapter", "updateItems called with ${newPillList.size} items")
        pillList.clear()
        pillList.addAll(newPillList)
        query = newQuery // 검색어 업데이트
        notifyDataSetChanged() // 데이터 변경 알림
    }
}