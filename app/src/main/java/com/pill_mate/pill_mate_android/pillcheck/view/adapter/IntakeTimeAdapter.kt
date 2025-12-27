package com.pill_mate.pill_mate_android.pillcheck.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.pill_mate.pill_mate_android.databinding.ItemTimeHeaderBinding
import com.pill_mate.pill_mate_android.pillcheck.model.MedicineCheckData
import com.pill_mate.pill_mate_android.pillcheck.model.ResponseHome.Data
import com.pill_mate.pill_mate_android.pillcheck.model.TimeGroup
import com.pill_mate.pill_mate_android.pillcheck.view.adapter.IntakeTimeAdapter.IntakeTimeViewHolder

class IntakeTimeAdapter(
    private val timeGroups: List<TimeGroup>, private val onCheckedChange: (List<MedicineCheckData>) -> Unit
) : RecyclerView.Adapter<IntakeTimeViewHolder>() {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    // [로그] 체크 완료(ON)일 때만 전체체크 로그 전송
    private fun logMedicationCheck(isCheckedNow: Boolean, isAllSelection: Boolean, medicationCount: Int) {
        if (isCheckedNow) {
            firebaseAnalytics.logEvent("check_medication") {
                param("is_all_checked", isAllSelection.toString())
                param("check_count", medicationCount.toLong())
            }
        }
    }

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
                adapter = MedicineAdapter(timeGroup.medicines, onCheckedChange)
            }

            binding.btnCheckAll.visibility = if (timeGroup.medicines.size >= 2) View.VISIBLE else View.GONE
            updateCheckAllButtonText(timeGroup)

            binding.btnCheckAll.setOnClickListener {
                val allChecked = timeGroup.medicines.all { it.eatCheck }
                val willBeChecked = !allChecked

                // [로그] 전체체크로 ON이 되는 경우에만 로그 전송
                if (willBeChecked) {
                    logMedicationCheck(
                        isCheckedNow = true, isAllSelection = true, medicationCount = timeGroup.medicines.size
                    )
                }

                updateCheckAllButtonText(timeGroup)

                val updatedCheckList = timeGroup.medicines.map { medicine ->
                    MedicineCheckData(medicine.medicineScheduleId, !allChecked)
                }

                onCheckedChange(updatedCheckList)

            }
        }

        private fun updateCheckAllButtonText(timeGroup: TimeGroup) {
            val allChecked = timeGroup.medicines.all { it.eatCheck }
            binding.btnCheckAll.text = if (allChecked) "전체체크 해제" else "전체체크"
        }
    }

    fun formatTimeWithAmPm(time: String): String { // time은 "HH:mm:ss" 형식으로 전달
        val hour = time.substring(0, 2).toInt()
        val minute = time.substring(3, 5)

        val amPm = if (hour < 12) "오전" else "오후"
        val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        return "$amPm $formattedHour:$minute"
    }

    fun formatMealUnitAndTime(medicines: List<Data>): String? {
        if (medicines.isEmpty()) return ""

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