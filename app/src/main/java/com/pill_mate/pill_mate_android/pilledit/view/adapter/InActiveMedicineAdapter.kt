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
import com.pill_mate.pill_mate_android.pilledit.view.adapter.InActiveMedicineAdapter.InActiveMedicineViewHolder

class InActiveMedicineAdapter(
    private val inActiveMedicineList: MutableList<MedicineItemData>,
    private val onEditClickListener: (MedicineItemData) -> Unit
) : RecyclerView.Adapter<InActiveMedicineViewHolder>() {

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
                tvMedicationPeriod.text =
                    DateUtil.formatPeriod(medicine.startDate, medicine.endDate, medicine.intakePeriod)

                val noInfo = binding.root.context.getString(R.string.no_info)
                tvMedicineName.text = if (medicine.medicineName.isNullOrBlank()) noInfo else medicine.medicineName
                tvMedicineEntp.text = if (medicine.entpName.isNullOrBlank()) noInfo else medicine.entpName
                tvMedicineType.text = if (medicine.className.isNullOrBlank()) noInfo else medicine.className
                divider.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE
                tvMedicineStatus.visibility = View.VISIBLE
                layoutBtn.visibility = View.GONE
            }

            Glide.with(binding.imgMedicine.context).load(medicine.image).error(R.drawable.img_default).transform(
                RoundedCorners(4)
            ).placeholder(R.drawable.img_default).into(binding.imgMedicine)

            //binding.root.setOnClickListener(null)

            /*binding.btnEdit.setOnClickListener {
                onEditClickListener.invoke(medicine)
            }

             */
        }
    }
}