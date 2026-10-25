package com.pill_mate.pill_mate_android.pillstats

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemStatsCalendarDayBinding

class PillStatsCalendarAdapter : RecyclerView.Adapter<PillStatsCalendarAdapter.DayViewHolder>() {

    // day == 0 은 달의 첫 주 앞쪽을 채우는 빈 칸
    data class DayCell(val day: Int, val isTaken: Boolean = false)

    private var cells: List<DayCell> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitCells(newCells: List<DayCell>) {
        cells = newCells
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemStatsCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) = holder.bind(cells[position])

    override fun getItemCount() = cells.size

    class DayViewHolder(private val binding: ItemStatsCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cell: DayCell) {
            val dayView = binding.tvDay
            if (cell.day == 0) {
                dayView.visibility = View.INVISIBLE
                return
            }

            dayView.visibility = View.VISIBLE
            dayView.text = cell.day.toString()
            dayView.setBackgroundResource(
                if (cell.isTaken) R.drawable.bg_stats_day_taken else R.drawable.bg_stats_day_default
            )
            dayView.setTextColor(
                ContextCompat.getColor(dayView.context, if (cell.isTaken) R.color.white else R.color.black)
            )
        }
    }
}
