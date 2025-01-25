package com.example.pill_mate_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pill_mate_android.databinding.ItemConflictBinding
import com.example.pill_mate_android.pillSearch.model.Conflict
import com.example.pill_mate_android.pillSearch.model.Hospital
import com.example.pill_mate_android.pillSearch.model.Pharmacy

class ConflictAdapter : ListAdapter<Conflict, ConflictAdapter.ConflictViewHolder>(ConflictDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConflictViewHolder {
        val binding = ItemConflictBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConflictViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConflictViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ConflictViewHolder(private val binding: ItemConflictBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conflict: Conflict) {
            with(binding) {
                ivImage.load(conflict.conflictingMedicine.image) {
                    placeholder(R.drawable.ic_default_pill)
                    error(R.drawable.ic_default_pill)
                }

                tvClassName.text = conflict.conflictingMedicine.ingredient
                tvPillName.text = conflict.conflictingMedicine.name
                tvCompanyName.text = conflict.conflictingMedicine.manufacturer

                if (conflict.crashName == "병용금기") {
                    tvWarning1.visibility = View.GONE
                    tvWarning2.visibility = View.GONE
                    tvWarningDetail.text = conflict.prohibitContent
                } else {
                    tvWarning1.visibility = View.VISIBLE
                    tvWarning2.visibility = View.VISIBLE
                    tvWarning1.text = when (conflict.crashName) {
                        "효능군 중복" -> "겹치는 효능군명"
                        "동일성분 중복" -> "동일성분 중복"
                        else -> ""
                    }
                    tvWarning2.text = conflict.crashName
                    tvWarningDetail.text = conflict.prohibitContent
                }

                btnDelete.setOnClickListener {
                    PillDeleteDialogFragment.newInstance(conflict.conflictingMedicine.name) {
                        // 삭제 완료 시 버튼 비활성화
                        btnDelete.isEnabled = false
                        btnDelete.alpha = 0.5f // 비활성화된 것처럼 시각적 표시 추가
                    }.show((itemView.context as AppCompatActivity).supportFragmentManager, "PillDeleteDialog")
                }

                btnInquiry.setOnClickListener {
                    // 약국 및 병원 정보를 전달하여 InquiryBottomSheetFragment 호출
                    val pharmacy = Pharmacy(
                        pharmacyName = conflict.pharmacy.pharmacyName,
                        pharmacyAddress = conflict.pharmacy.pharmacyAddress,
                        pharmacyPhone = conflict.pharmacy.pharmacyPhone
                    )

                    val hospital = conflict.hospital?.hospitalName?.let {
                        Hospital(
                            hospitalName = it,
                            hospitalAddress = conflict.hospital.hospitalAddress ?: "",
                            hospitalPhone = conflict.hospital.hospitalPhone ?: ""
                        )
                    }

                    InquiryBottomSheetFragment.newInstance(pharmacy, hospital)
                        .show((itemView.context as AppCompatActivity).supportFragmentManager, "InquiryBottomSheet")
                }
            }
        }
    }
}

class ConflictDiffCallback : DiffUtil.ItemCallback<Conflict>() {
    override fun areItemsTheSame(oldItem: Conflict, newItem: Conflict): Boolean {
        return oldItem.conflictingMedicine.name == newItem.conflictingMedicine.name
    }

    override fun areContentsTheSame(oldItem: Conflict, newItem: Conflict): Boolean {
        return oldItem == newItem
    }
}