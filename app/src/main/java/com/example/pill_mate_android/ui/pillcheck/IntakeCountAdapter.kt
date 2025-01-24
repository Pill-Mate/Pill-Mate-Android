package com.example.pill_mate_android.ui.pillcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.R
import com.example.pill_mate_android.databinding.ItemCountHeaderBinding

class IntakeCountAdapter(
    private val groupedMedicines: List<GroupedMedicine>
) : RecyclerView.Adapter<IntakeCountAdapter.IntakeCountViewHolder>() {

    private val expandedStates = mutableSetOf<Int>() // 각 아이템의 확장 상태를 저장

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeCountViewHolder {
        val binding = ItemCountHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntakeCountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntakeCountViewHolder, position: Int) {
        val group = groupedMedicines[position]
        val isExpanded = expandedStates.contains(position)

        holder.bind(group, isExpanded)

        // [추가구현] : 아이템 클릭시 드롭다운 이미지 변경되어야 함
        holder.itemView.setOnClickListener {
            if (isExpanded) expandedStates.remove(position) else expandedStates.add(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = groupedMedicines.size

    inner class IntakeCountViewHolder(private val binding: ItemCountHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: GroupedMedicine, isExpanded: Boolean) {
            binding.tvIntakeCount.text = when (group.intakeCount) {
                "EMPTY" -> "공복"
                "MORNING" -> "아침"
                "LUNCH" -> "점심"
                "DINNER" -> "저녁"
                else -> "취침"
            }

            val iconResId = when (group.intakeCount) {
                "EMPTY" -> R.drawable.ic_empty
                "MORNING" -> R.drawable.ic_morning
                "LUNCH" -> R.drawable.ic_lunch
                "DINNER" -> R.drawable.ic_dinner
                else -> R.drawable.ic_sleep
            }
            binding.icIntakeCount.setImageResource(iconResId)

            // 확장 상태에 따라 드롭다운 아이콘 설정
            binding.btnDropdown.setImageResource(
                if (isExpanded) R.drawable.btn_dropdown_down else R.drawable.btn_dropdown_up
            )

            binding.intakeTimeRecyclerView.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                visibility = if (isExpanded) View.VISIBLE else View.GONE
                adapter = IntakeTimeAdapter(group.times)
            }
        }
    }
}
