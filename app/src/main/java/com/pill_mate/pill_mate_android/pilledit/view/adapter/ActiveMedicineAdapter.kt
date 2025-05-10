package com.pill_mate.pill_mate_android.pilledit.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.ItemActiveInactiveMedicineBinding
import com.pill_mate.pill_mate_android.pilledit.model.MedicineItemData
import com.pill_mate.pill_mate_android.pilledit.util.DateUtil
import com.pill_mate.pill_mate_android.pilledit.view.adapter.ActiveMedicineAdapter.ActiveMedicineViewHolder

class ActiveMedicineAdapter(
    private val activeMedicineList: MutableList<MedicineItemData>,
    private val onEditClickListener: (MedicineItemData) -> Unit,
    private val onStopClickListener: (MedicineItemData) -> Unit,
) : RecyclerView.Adapter<ActiveMedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveMedicineViewHolder {
        val binding = ItemActiveInactiveMedicineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ActiveMedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActiveMedicineViewHolder, position: Int) {
        val isLastItem = position == activeMedicineList.size - 1
        holder.onBind(activeMedicineList[position], isLastItem)
    }

    override fun getItemCount(): Int = activeMedicineList.size

    fun updateList(newList: List<MedicineItemData>) {
        activeMedicineList.clear()
        activeMedicineList.addAll(newList)
        notifyDataSetChanged() // UI 갱신
    }

    inner class ActiveMedicineViewHolder(private val binding: ItemActiveInactiveMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(medicine: MedicineItemData, isLastItem: Boolean) {
            with(binding) {
                tvMedicationPeriod.text =
                    DateUtil.formatPeriod(medicine.startDate, medicine.endDate, medicine.intakePeriod)
                tvMedicineName.text = medicine.medicineName
                tvMedicineEntp.text = medicine.entpName
                tvMedicineType.text = medicine.className
                divider.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE // 복용중지 태그 안보이게 설정
                tvMedicineStatus.visibility = View.INVISIBLE
            }

            Glide.with(binding.imgMedicine.context).load(medicine.image).error(R.drawable.img_default).transform(
                RoundedCorners(4)
            ).placeholder(R.drawable.img_default).into(binding.imgMedicine)

            binding.btnEdit.setOnClickListener {
                onEditClickListener.invoke(medicine)
            }

            binding.btnStop.setOnClickListener {
                onStopClickListener(medicine)
            }
        }
    }
}