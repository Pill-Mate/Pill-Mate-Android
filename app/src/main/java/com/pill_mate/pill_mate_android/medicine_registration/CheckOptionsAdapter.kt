package com.pill_mate.pill_mate_android.medicine_registration

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
        val checkBox: CheckBox = view.findViewById(R.id.cb_option_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val option = options[position]
        holder.textView.text = option

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = (position == selectedPosition)

        // 애니메이션 제거
        holder.checkBox.jumpDrawablesToCurrentState()

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            selectedPosition = if (selectedPosition == currentPosition) {
                null
            } else {
                currentPosition
            }

            notifyDataSetChanged()
            onOptionClick(if (selectedPosition != null) options[selectedPosition!!] else null)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPosition = position
                notifyDataSetChanged()
                onOptionClick(option)
            } else {
                selectedPosition = null
                notifyDataSetChanged()
                onOptionClick(null)
            }
        }
    }

    override fun getItemCount(): Int = options.size
}