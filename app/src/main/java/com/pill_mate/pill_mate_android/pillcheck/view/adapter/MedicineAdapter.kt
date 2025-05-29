package com.pill_mate.pill_mate_android.pillcheck.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemMedicineBinding
import com.pill_mate.pill_mate_android.pillcheck.model.MedicineCheckData
import com.pill_mate.pill_mate_android.pillcheck.model.ResponseHome.Data
import com.pill_mate.pill_mate_android.pillcheck.view.adapter.MedicineAdapter.MedicineViewHolder

class MedicineAdapter(
    private val medicines: List<Data>, private val onCheckedChange: (List<MedicineCheckData>) -> Unit
) : RecyclerView.Adapter<MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = ItemMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val isLastItem = position == medicines.size - 1 // 마지막 아이템 여부 확인
        holder.bind(medicines[position], isLastItem)
    }

    override fun getItemCount() = medicines.size

    inner class MedicineViewHolder(private val binding: ItemMedicineBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicine: Data, isLastItem: Boolean) {
            binding.tvMedicineName.text = medicine.medicineName
            binding.cbCheck.isChecked = medicine.eatCheck

            val unit = when (medicine.eatUnit) {
                "JUNG" -> "정"
                "CAPSULE" -> "캡슐"
                "ML" -> "ML"
                "POH" -> "포"
                else -> "주사"
            }
            binding.tvEatCount.text = "${medicine.eatCount}${unit}"

            // Glide로 이미지 로드
            Glide.with(binding.root.context).load(medicine.medicineImage).error(R.drawable.img_default).transform(
                RoundedCorners(4)
            ).placeholder(R.drawable.img_default).into(binding.imgMedicine)

            // 마지막 아이템일때 하단 모서리 둥글게
            val backgroudResId = if (isLastItem) {
                R.drawable.shape_bottom_round_item
            } else {
                R.drawable.shape_normal_item
            }
            binding.itemMedicine.setBackgroundResource(backgroudResId)
            binding.itemLine.visibility = if (medicines.size >= 2 && !isLastItem) View.VISIBLE else View.INVISIBLE

            // 체크박스 상태 및 클릭 이벤트 설정
            binding.cbCheck.setOnClickListener {
                val newCheckState = binding.cbCheck.isChecked
                val checkDataList = listOf(MedicineCheckData(medicine.medicineScheduleId, newCheckState))
                onCheckedChange(checkDataList)
            }
        }
    }
}