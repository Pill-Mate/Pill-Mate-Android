package com.pill_mate.pill_mate_android.medicine_registration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pill_mate.pill_mate_android.R

class CheckOptionsAdapter(
    private val options: List<String>,
    private var selectedOption: String?,
    private val onOptionClick: (String?) -> Unit // 선택 항목이 없을 경우 null을 전달
) : RecyclerView.Adapter<CheckOptionsAdapter.ViewHolder>() {

    private var selectedPosition: Int? = options.indexOf(selectedOption) // 초기 선택된 항목의 위치

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_option)
        val imageView: ImageView = view.findViewById(R.id.iv_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]

        holder.textView.text = option

        holder.imageView.setImageResource(
            if (position == selectedPosition) R.drawable.ic_btn_select
            else R.drawable.ic_btn_unselect
        )

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            if (selectedPosition == currentPosition) {
                // 🔥 현재 선택된 항목을 다시 클릭한 경우 선택 해제
                val previousPosition = selectedPosition
                selectedPosition = null // 선택 해제
                notifyItemChanged(previousPosition!!) // 이전 항목 갱신
                onOptionClick(null) // 선택 해제
            } else {
                // 새로 선택한 경우
                val previousPosition = selectedPosition
                selectedPosition = currentPosition

                // 이전 선택 항목 해제
                previousPosition?.let { notifyItemChanged(it) }
                // 새로 선택한 항목 갱신
                notifyItemChanged(currentPosition)
                onOptionClick(option) // 선택한 옵션 전달
            }
        }
    }

    override fun getItemCount(): Int = options.size

    // 선택된 옵션을 반환하는 메서드
    fun getSelectedOption(): String? {
        return if (selectedPosition != null) options[selectedPosition!!] else null
    }

    // 선택된 옵션의 위치가 있는지 확인하는 메서드
    fun hasSelectedOption(): Boolean {
        return selectedPosition != null
    }
}