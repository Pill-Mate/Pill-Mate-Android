package com.example.pill_mate_android.ui.pillcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pill_mate_android.databinding.ItemTimeHeaderBinding

class IntakeTimeAdapter(
    private val timeGroups: List<TimeGroup>
) : RecyclerView.Adapter<IntakeTimeAdapter.IntakeTimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeTimeViewHolder {
        val binding = ItemTimeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntakeTimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntakeTimeViewHolder, position: Int) {
        val timeGroup = timeGroups[position]
        holder.bind(timeGroup)
    }

    override fun getItemCount() = timeGroups.size

    inner class IntakeTimeViewHolder(private val binding: ItemTimeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(timeGroup: TimeGroup) {
            binding.tvTime.text = formatTimeWithAmPm(timeGroup.intakeTime)
            val mealUnitAndTime = formatMealUnitAndTime(timeGroup.medicines)
            if (mealUnitAndTime == null) {
                binding.tvMealUnit.visibility = View.INVISIBLE
            } else {
                binding.tvMealUnit.text = mealUnitAndTime
            }

            binding.medicineRecyclerView.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = MedicineAdapter(timeGroup.medicines)
            }
        }
    }

    fun formatTimeWithAmPm(time: String): String { // time은 "HH:mm:ss" 형식으로 전달
        val hour = time.substring(0, 2).toInt()
        val minute = time.substring(3, 5)

        val amPm = if (hour < 12) "오전" else "오후"
        val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        return "$amPm $formattedHour:$minute"
    }

    fun formatMealUnitAndTime(medicines: List<ResponseHome.Data>): String? {
        if (medicines.isEmpty()) return "" // 리스트가 비어있으면 빈 문자열 반환

        val firstMedicine = medicines[0] // 첫 번째 약 정보 기준으로 처리
        val mealUnitText = when (firstMedicine.mealUnit) {
            "MEALBEFORE" -> "식전"
            "MEALAFTER" -> "식후"
            else -> null // 공복, 취침전일 경우
        }
        if (mealUnitText == null) {
            return null
        } else {
            return "$mealUnitText ${firstMedicine.mealTime}분"
        }
    }
}