package com.pill_mate.pill_mate_android.ui.pilledit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemActiveInactiveMedicineBinding
import java.text.SimpleDateFormat
import java.util.*

class InActiveMedicineAdapter(
    private val inActiveMedicineList: MutableList<MedicineItemData>,
    private val onEditClickListener: (MedicineItemData) -> Unit
) : RecyclerView.Adapter<InActiveMedicineAdapter.InActiveMedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InActiveMedicineViewHolder {
        val binding = ItemActiveInactiveMedicineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InActiveMedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InActiveMedicineViewHolder, position: Int) {
        val isLastItem = position == inActiveMedicineList.size - 1
        holder.onBind(inActiveMedicineList[position], isLastItem)
    }

    override fun getItemCount(): Int = inActiveMedicineList.size

    fun updateList(newList: List<MedicineItemData>) {
        inActiveMedicineList.clear()
        inActiveMedicineList.addAll(newList)
        notifyDataSetChanged() // UI 갱신
    }

    inner class InActiveMedicineViewHolder(private val binding: ItemActiveInactiveMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(medicine: MedicineItemData, isLastItem: Boolean) {
            with(binding) {
                tvMedicationPeriod.text = formatPeriod(medicine.startDate, medicine.endDate, medicine.intakePeriod)
                tvMedicineName.text = medicine.medicineName
                tvMedicineEntp.text = medicine.entpName
                tvMedicineType.text = medicine.className
                divider.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE // 복용중지 태그 안보이게 설정
                tvMedicineStatus.visibility = View.INVISIBLE
            }

            Glide.with(binding.imgMedicine.context).load(medicine.image).placeholder(R.drawable.img_default)
                .into(binding.imgMedicine)

            //binding.root.setOnClickListener(null)

            /*binding.btnEdit.setOnClickListener {
                onEditClickListener.invoke(medicine)
            }

             */
        }
    }

    // 날짜 변환 함수
    private fun formatPeriod(startDate: String, endDate: String, intakePeriod: Int): String {
        val formattedStartDate = formatDate(startDate, includeYear = true) // "2025.01.01"
        val formattedEndDate = formatDate(endDate, includeYear = false) // "02.01"
        return "$formattedStartDate~$formattedEndDate · ${intakePeriod}일"
    }

    private fun formatDate(date: String, includeYear: Boolean): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val yearFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val monthDayFormat = SimpleDateFormat("MM.dd", Locale.getDefault())

            val parsedDate = inputFormat.parse(date)
            parsedDate?.let {
                if (includeYear) yearFormat.format(it) else monthDayFormat.format(it)
            } ?: date
        } catch (e: Exception) {
            date // 변환 실패 시 원래 날짜 반환
        }
    }
}