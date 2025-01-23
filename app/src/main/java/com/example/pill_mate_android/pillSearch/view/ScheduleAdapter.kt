package com.example.pill_mate_android.pillSearch.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.ItemScheduleBinding

class ScheduleAdapter(private var scheduleList: List<ScheduleItem>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    data class ScheduleItem(
        val iconRes: Int, // 시간대 아이콘
        val label: String, // 시간대 이름 (아침, 공복 등)
        val time: String, // 시간 (예: 오전 8:00)
        val mealTime: String? = null // 추가 정보 (예: 식전 30분, 공복 등)
    )

    inner class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem) {
            binding.ivIcon.setImageResource(item.iconRes)
            binding.tvLabel.text = item.label
            binding.tvTime.text = item.time

            // 공복과 취침전의 경우 mealTime을 숨김
            if (item.label == "공복" || item.label == "취침전") {
                binding.tvMealTime.visibility = View.GONE
            } else if (item.mealTime != null) {
                binding.tvMealTime.text = item.mealTime
                binding.tvMealTime.visibility = View.VISIBLE
            } else {
                binding.tvMealTime.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        val binding = ItemScheduleBinding.bind(view)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateScheduleList(newList: List<ScheduleItem>) {
        scheduleList = newList
        notifyDataSetChanged()
    }
}