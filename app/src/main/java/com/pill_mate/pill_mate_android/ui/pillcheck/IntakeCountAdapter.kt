package com.pill_mate.pill_mate_android.ui.pillcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemCountHeaderBinding

class IntakeCountAdapter(
    private var groupedMedicines: List<GroupedMedicine>,
    private val expandedStates: MutableSet<Int>,
    private val onCheckedChange: (List<MedicineCheckData>) -> Unit
) : RecyclerView.Adapter<IntakeCountAdapter.IntakeCountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeCountViewHolder {
        val binding = ItemCountHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntakeCountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntakeCountViewHolder, position: Int) {
        val group = groupedMedicines[position]
        val isExpanded = expandedStates.contains(position)

        holder.bind(group, isExpanded)

        holder.itemView.setOnClickListener {
            if (isExpanded) expandedStates.remove(position) else expandedStates.add(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = groupedMedicines.size

    fun updateData(newGroupedMedicines: List<GroupedMedicine>) {
        val diffCallback = IntakeCountDiffUtil(groupedMedicines, newGroupedMedicines)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        groupedMedicines = newGroupedMedicines
        updateExpandedStates()
        diffResult.dispatchUpdatesTo(this) // 변경된 부분만 UI 갱신
    }

    fun updateExpandedStates() {
        expandedStates.clear()
        groupedMedicines.forEachIndexed { index, group ->
            if (!group.isAllChecked) {
                expandedStates.add(index)
            }
        }
    }

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
                adapter = IntakeTimeAdapter(group.times, onCheckedChange)
            }
        }
    }
}
